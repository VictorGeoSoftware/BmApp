package com.briel.marnisos.brielapp.data.repository

import com.briel.marnisos.brielapp.data.network.UserAccessApi
import com.briel.marnisos.brielapp.domain.models.AuthUserModel
import com.briel.marnisos.brielapp.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ReportAccessRepositoryImplTest {

    private val premium = """{"tier":"PREMIUM","capabilities":["COMPREHENSIVE_COMPARATOR_PDF"]}"""
    private val basic = """{"tier":"BASIC","capabilities":[]}"""

    private var now = 1_000_000L
    private var currentUid: String? = "uid-a"
    private val authRepository = mockk<AuthRepository> {
        every { getCurrentUser() } answers { currentUid?.let { AuthUserModel(uid = it, email = null, displayName = null, photoUrl = null, providerIds = emptyList()) } }
        coEvery { getIdToken(false) } returns Result.success("token")
    }
    private val api = mockk<UserAccessApi>()
    private val repository = ReportAccessRepositoryImpl(authRepository, api) { now }

    private val allowed get() = repository.access.value.canRequestComprehensiveReport

    @Test
    fun `T13 initial state is hidden`() {
        assertFalse(allowed)
    }

    @Test
    fun `T1 capability allows`() = runTest {
        coEvery { api.fetchAccess("token") } returns Result.success(premium)
        repository.refresh()
        assertTrue(allowed)
    }

    @Test
    fun `T2 T5 basic and malformed hide`() = runTest {
        coEvery { api.fetchAccess(any()) } returns Result.success(basic)
        repository.refresh()
        assertFalse(allowed)
        now += 60_000
        coEvery { api.fetchAccess(any()) } returns Result.success("nope")
        repository.refresh()
        assertFalse(allowed)
    }

    @Test
    fun `T6 T7 failures hide and never sign out`() = runTest {
        coEvery { api.fetchAccess(any()) } returns Result.failure(IllegalStateException("403"))
        repository.refresh()
        assertFalse(allowed)
        coVerify(exactly = 0) { authRepository.logout() }
        io.mockk.verify(exactly = 0) { authRepository.signOutLocally() }
    }

    @Test
    fun `T8 no user means hidden and no request`() = runTest {
        currentUid = null
        repository.refresh()
        assertFalse(allowed)
        coVerify(exactly = 0) { api.fetchAccess(any()) }
    }

    @Test
    fun `T8 no token means hidden and no request`() = runTest {
        coEvery { authRepository.getIdToken(false) } returns Result.failure(IllegalStateException())
        repository.refresh()
        assertFalse(allowed)
        coVerify(exactly = 0) { api.fetchAccess(any()) }
    }

    @Test
    fun `T9 downgrade after throttle hides`() = runTest {
        coEvery { api.fetchAccess(any()) } returns Result.success(premium)
        repository.refresh()
        assertTrue(allowed)
        coEvery { api.fetchAccess(any()) } returns Result.success(basic)
        now += 59_999
        repository.refresh()
        assertTrue(allowed, "throttled within 60 s")
        now += 1
        repository.refresh()
        assertFalse(allowed)
        coVerify(exactly = 2) { api.fetchAccess(any()) }
    }

    @Test
    fun `T9 failure after shown hides`() = runTest {
        coEvery { api.fetchAccess(any()) } returns Result.success(premium)
        repository.refresh()
        coEvery { api.fetchAccess(any()) } returns Result.failure(RuntimeException("5xx"))
        now += 60_000
        repository.refresh()
        assertFalse(allowed)
    }

    @Test
    fun `T10 upgrade shows`() = runTest {
        coEvery { api.fetchAccess(any()) } returns Result.success(basic)
        repository.refresh()
        coEvery { api.fetchAccess(any()) } returns Result.success(premium)
        now += 60_000
        repository.refresh()
        assertTrue(allowed)
    }

    @Test
    fun `T12 account switch resets and first check for the new uid is immediate`() = runTest {
        coEvery { api.fetchAccess(any()) } returns Result.success(premium)
        repository.refresh()
        assertTrue(allowed)
        currentUid = "uid-b"
        coEvery { api.fetchAccess(any()) } returns Result.success(basic)
        repository.refresh()
        assertFalse(allowed)
        coVerify(exactly = 2) { api.fetchAccess(any()) }
    }

    @Test
    fun `T12 clear resets to hidden and allows an immediate re-check`() = runTest {
        coEvery { api.fetchAccess(any()) } returns Result.success(premium)
        repository.refresh()
        repository.clear()
        assertFalse(allowed)
        repository.refresh()
        assertTrue(allowed)
        coVerify(exactly = 2) { api.fetchAccess(any()) }
    }

    @Test
    fun `T11 response for a uid that is no longer current is discarded`() = runTest {
        val gate = CompletableDeferred<Result<String>>()
        coEvery { api.fetchAccess(any()) } coAnswers { gate.await() }
        val pending = async { repository.refresh() }
        yield()
        currentUid = "uid-b"
        gate.complete(Result.success(premium))
        pending.await()
        assertFalse(allowed)
    }

    @Test
    fun `T11 response arriving after clear is discarded`() = runTest {
        val gate = CompletableDeferred<Result<String>>()
        coEvery { api.fetchAccess(any()) } coAnswers { gate.await() }
        val pending = async { repository.refresh() }
        yield()
        repository.clear()
        gate.complete(Result.success(premium))
        pending.await()
        assertFalse(allowed)
    }

    @Test
    fun `T11 older request finishing after a newer one is discarded`() = runTest {
        val first = CompletableDeferred<Result<String>>()
        val second = CompletableDeferred<Result<String>>()
        var calls = 0
        coEvery { api.fetchAccess(any()) } coAnswers { if (calls++ == 0) first.await() else second.await() }
        val older = async { repository.refresh() }
        yield()
        now += 60_000
        val newer = async { repository.refresh() }
        yield()
        second.complete(Result.success(basic))
        newer.await()
        first.complete(Result.success(premium))
        older.await()
        assertFalse(allowed)
        assertEquals(2, calls)
    }

    @Test
    fun `forceRefresh hides immediately and bypasses the throttle (A7 A8)`() = runTest {
        coEvery { api.fetchAccess(any()) } returns Result.success(premium)
        repository.refresh()
        assertTrue(allowed)
        val gate = CompletableDeferred<Result<String>>()
        coEvery { api.fetchAccess(any()) } coAnswers { gate.await() }
        now += 1_000
        val pending = async { repository.forceRefresh() }
        yield()
        assertFalse(allowed, "hidden before the backend answers")
        gate.complete(Result.success(basic))
        pending.await()
        assertFalse(allowed)
        coVerify(exactly = 2) { api.fetchAccess(any()) }
        coVerify(exactly = 0) { authRepository.logout() }
        io.mockk.verify(exactly = 0) { authRepository.signOutLocally() }
    }

    @Test
    fun `forceRefresh re-shows when the backend still grants the capability`() = runTest {
        coEvery { api.fetchAccess(any()) } returns Result.success(premium)
        repository.refresh()
        repository.forceRefresh()
        assertTrue(allowed)
        coVerify(exactly = 2) { api.fetchAccess(any()) }
    }

    @Test
    fun `forceRefresh without a user stays hidden and makes no request`() = runTest {
        currentUid = null
        repository.forceRefresh()
        assertFalse(allowed)
        coVerify(exactly = 0) { api.fetchAccess(any()) }
    }
}
