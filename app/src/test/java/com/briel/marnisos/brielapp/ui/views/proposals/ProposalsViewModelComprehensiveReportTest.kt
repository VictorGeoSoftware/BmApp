package com.briel.marnisos.brielapp.ui.views.proposals

import com.briel.marnisos.brielapp.R
import com.briel.marnisos.brielapp.domain.models.ComparatorReportPdfModel
import com.briel.marnisos.brielapp.domain.models.ComparatorSummaryModel
import com.briel.marnisos.brielapp.domain.models.ConsumptionSessionModel
import com.briel.marnisos.brielapp.domain.models.PremiumReportException
import com.briel.marnisos.brielapp.domain.models.PremiumReportFailure
import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel
import com.briel.marnisos.brielapp.domain.models.ReportAccessModel
import com.briel.marnisos.brielapp.domain.repository.ConsumptionSessionRepository
import com.briel.marnisos.brielapp.domain.usecases.CalculateComparatorSummaryUseCase
import com.briel.marnisos.brielapp.domain.usecases.ForceRefreshReportAccessUseCase
import com.briel.marnisos.brielapp.domain.usecases.GenerateComparatorReportPdfUseCase
import com.briel.marnisos.brielapp.domain.usecases.GeneratePremiumReportPdfUseCase
import com.briel.marnisos.brielapp.domain.usecases.ObserveCurrentUserConditionsUseCase
import com.briel.marnisos.brielapp.domain.usecases.ObserveReportAccessUseCase
import com.briel.marnisos.brielapp.domain.usecases.RefreshReportAccessUseCase
import com.briel.marnisos.brielapp.ui.views.pricetable.export.ComparatorPdfFileStore
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/** Truth table A1–A18 of docs/specs/report-android-export.md. */
@OptIn(ExperimentalCoroutinesApi::class)
class ProposalsViewModelComprehensiveReportTest {

    private data class Call(val jobId: String, val model: ComparatorReportPdfModel, val forceTokenRefresh: Boolean)

    private val proposal = ProposalPriceModel("Oferta A", listOf(1.0), 10.0, listOf(0.1), 20.0, 0.0, 6.3, 1.5, 37.8)
    private fun session(jobId: String = "job-1", holder: String = "Ana", address: String = "Calle 1") =
        ConsumptionSessionModel(jobId, "2.0TD", 21.0, 5.11, listOf("P1" to 3.3), listOf("P1" to 100), holder, address, "ES001", listOf(proposal))

    private val sessionFlow = MutableStateFlow<ConsumptionSessionModel?>(session())
    private val access = MutableStateFlow(ReportAccessModel(canRequestComprehensiveReport = true))
    private val sessionRepository = mockk<ConsumptionSessionRepository>(relaxed = true) {
        every { session } returns sessionFlow
        every { proposalVisibilityByTitle } returns MutableStateFlow(emptyMap())
        every { proposalFixedAmountByTitle } returns MutableStateFlow(mapOf("Oferta A" to "12.50"))
    }
    private val summaryUseCase = mockk<CalculateComparatorSummaryUseCase> {
        every { this@mockk.invoke(any(), any(), any()) } returns ComparatorSummaryModel(proposals = listOf(proposal))
    }

    private val calls = mutableListOf<Call>()
    private val results = ArrayDeque<suspend () -> Result<ByteArray>>()
    private val premiumUseCase = GeneratePremiumReportPdfUseCase { jobId, model, force ->
        calls += Call(jobId, model, force)
        results.removeFirst().invoke()
    }
    private var forceRefreshCalls = 0
    private val forceRefresh = ForceRefreshReportAccessUseCase {
        forceRefreshCalls++
        access.value = ReportAccessModel(false)
    }
    private val basicUseCase = GenerateComparatorReportPdfUseCase { basicCalls++; Result.success(byteArrayOf(9)) }
    private var basicCalls = 0
    private val storedFile = File("informe_completo_x.pdf")
    private val fileStore = mockk<ComparatorPdfFileStore> {
        every { store(any(), any()) } returns Result.success(storedFile)
    }

    private lateinit var viewModel: ProposalsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = ProposalsViewModel(
            consumptionSessionRepository = sessionRepository,
            observeCurrentUserConditionsUseCase = ObserveCurrentUserConditionsUseCase { flowOf(null) },
            calculateComparatorSummaryUseCase = summaryUseCase,
            selectUncompetitiveProposalsUseCase = mockk(relaxed = true),
            generateComparatorReportPdfUseCase = basicUseCase,
            comparatorPdfFileStore = fileStore,
            crashReporter = mockk(relaxed = true),
            analyticsTracker = mockk(relaxed = true),
            observeReportAccessUseCase = ObserveReportAccessUseCase { access },
            refreshReportAccessUseCase = RefreshReportAccessUseCase { },
            generatePremiumReportPdfUseCase = premiumUseCase,
            forceRefreshReportAccessUseCase = forceRefresh,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun TestScope.collect(): Job = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
    private val state get() = viewModel.uiState.value
    private val pdf = byteArrayOf(1, 2, 3)
    private fun fail(failure: PremiumReportFailure): suspend () -> Result<ByteArray> = { Result.failure(PremiumReportException(failure)) }

    private fun TestScope.assertOutcome(failure: PremiumReportFailure, message: Int, retry: Boolean) {
        results += fail(failure)
        viewModel.requestComprehensiveReport()
        assertEquals(ComprehensiveReportMessage(message, retry), state.comprehensiveReportMessage)
        assertFalse(state.isGeneratingComprehensiveReport)
        viewModel.dismissComprehensiveReportMessage()
    }

    @Test
    fun `A1 blank or placeholder holder shows S4 without a request`() = runTest {
        val job = collect()
        listOf("", "  ", "--").forEach { holder ->
            sessionFlow.value = session(holder = holder)
            viewModel.requestComprehensiveReport()
            assertEquals(ComprehensiveReportMessage(R.string.premium_report_holder_missing, false), state.comprehensiveReportMessage)
            viewModel.dismissComprehensiveReportMessage()
        }
        assertTrue(calls.isEmpty())
        job.cancel()
    }

    @Test
    fun `A2 blank or placeholder address shows S5 without a request`() = runTest {
        val job = collect()
        listOf("", "--").forEach { address ->
            sessionFlow.value = session(address = address)
            viewModel.requestComprehensiveReport()
            assertEquals(ComprehensiveReportMessage(R.string.premium_report_address_missing, false), state.comprehensiveReportMessage)
            viewModel.dismissComprehensiveReportMessage()
        }
        assertTrue(calls.isEmpty())
        job.cancel()
    }

    @Test
    fun `A3 no session means no request and no message`() = runTest {
        val job = collect()
        sessionFlow.value = null
        viewModel.requestComprehensiveReport()
        assertTrue(calls.isEmpty())
        assertNull(state.comprehensiveReportMessage)
        assertFalse(state.showsComprehensiveReportAction)
        job.cancel()
    }

    @Test
    fun `A4 A5 success stores informe_completo and emits the file for sharing, button re-enabled`() = runTest {
        val job = collect()
        val shared = mutableListOf<File>()
        val shareJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.comprehensiveReportFile.collect { shared += it } }
        results += { Result.success(pdf) }
        viewModel.requestComprehensiveReport()

        assertEquals(1, calls.size)
        assertEquals("job-1", calls[0].jobId)
        assertFalse(calls[0].forceTokenRefresh)
        val model = calls[0].model
        assertEquals("Ana", model.supplyHolder)
        assertEquals("Calle 1", model.supplyAddress)
        assertEquals(listOf("Oferta A"), model.proposals.map { it.title })
        assertEquals("12.50", model.proposals[0].extraServices)
        verify { fileStore.store(pdf, "informe_completo") }
        assertEquals(listOf(storedFile), shared)
        assertNull(state.comprehensiveReportMessage)
        assertFalse(state.isGeneratingComprehensiveReport)
        assertTrue(state.showsComprehensiveReportAction)
        shareJob.cancel(); job.cancel()
    }

    @Test
    fun `A6 storing fails shows S8 with retry`() = runTest {
        val job = collect()
        every { fileStore.store(any(), any()) } returns Result.failure(java.io.IOException("disk"))
        results += { Result.success(pdf) }
        viewModel.requestComprehensiveReport()
        assertEquals(ComprehensiveReportMessage(R.string.premium_report_generation_failed, true), state.comprehensiveReportMessage)
        job.cancel()
    }

    @Test
    fun `A7 premium_required shows S1, hides the action at once and forces a re-check, no sign-out`() = runTest {
        val job = collect()
        assertOutcome(PremiumReportFailure.PREMIUM_REQUIRED, R.string.premium_report_required, retry = false)
        assertEquals(1, forceRefreshCalls)
        assertFalse(state.showsComprehensiveReportAction)
        verify(exactly = 0) { sessionRepository.clear() }
        job.cancel()
    }

    @Test
    fun `A8 other 403 shows E14, hides and forces a re-check`() = runTest {
        val job = collect()
        assertOutcome(PremiumReportFailure.ACCESS_REVOKED, R.string.premium_report_access_revoked, retry = false)
        assertEquals(1, forceRefreshCalls)
        assertFalse(state.showsComprehensiveReportAction)
        job.cancel()
    }

    @Test
    fun `A9 401 shows S8 with retry and retry fetches a fresh token`() = runTest {
        val job = collect()
        results += fail(PremiumReportFailure.UNAUTHENTICATED)
        viewModel.requestComprehensiveReport()
        assertEquals(ComprehensiveReportMessage(R.string.premium_report_generation_failed, true), state.comprehensiveReportMessage)
        assertEquals(0, forceRefreshCalls)
        assertTrue(state.showsComprehensiveReportAction)
        results += { Result.success(pdf) }
        viewModel.retryComprehensiveReport()
        assertEquals(2, calls.size)
        assertTrue(calls[1].forceTokenRefresh)
        assertEquals(calls[0].model, calls[1].model)
        job.cancel()
    }

    @Test
    fun `A10 to A17 messages and retry per code`() = runTest {
        val job = collect()
        assertOutcome(PremiumReportFailure.STUDY_NOT_FOUND, R.string.premium_report_study_expired, false)
        assertOutcome(PremiumReportFailure.STUDY_NOT_READY, R.string.premium_report_study_not_ready, true)
        assertOutcome(PremiumReportFailure.HOLDER_MISSING, R.string.premium_report_holder_missing, false)
        assertOutcome(PremiumReportFailure.ADDRESS_MISSING, R.string.premium_report_address_missing, false)
        assertOutcome(PremiumReportFailure.CONDITIONS_MISSING, R.string.premium_report_conditions_missing, false)
        assertOutcome(PremiumReportFailure.DATA_MISMATCH, R.string.premium_report_data_mismatch, true)
        assertOutcome(PremiumReportFailure.UNAVAILABLE, R.string.premium_report_unavailable, false)
        assertOutcome(PremiumReportFailure.GENERATION_FAILED, R.string.premium_report_generation_failed, true)
        assertEquals(0, forceRefreshCalls)
        assertTrue(state.showsComprehensiveReportAction)
        job.cancel()
    }

    @Test
    fun `A10 404 keeps session inputs and makes no refetch`() = runTest {
        val job = collect()
        assertOutcome(PremiumReportFailure.STUDY_NOT_FOUND, R.string.premium_report_study_expired, false)
        verify(exactly = 0) { sessionRepository.clear() }
        verify(exactly = 0) { sessionRepository.setProposalFixedAmount(any(), any()) }
        assertEquals("job-1", sessionFlow.value?.jobId)
        job.cancel()
    }

    @Test
    fun `A17 an unexpected exception is a generation failure`() = runTest {
        val job = collect()
        results += { Result.failure(IllegalStateException("boom")) }
        viewModel.requestComprehensiveReport()
        assertEquals(ComprehensiveReportMessage(R.string.premium_report_generation_failed, true), state.comprehensiveReportMessage)
        job.cancel()
    }

    @Test
    fun `A18 a second tap while in flight is ignored`() = runTest {
        val job = collect()
        val gate = CompletableDeferred<Result<ByteArray>>()
        results += { gate.await() }
        viewModel.requestComprehensiveReport()
        assertTrue(state.isGeneratingComprehensiveReport)
        viewModel.requestComprehensiveReport()
        viewModel.retryComprehensiveReport()
        assertEquals(1, calls.size)
        gate.complete(Result.success(pdf))
        assertFalse(state.isGeneratingComprehensiveReport)
        job.cancel()
    }

    @Test
    fun `retry re-sends the identical failed request even if inputs changed since`() = runTest {
        val job = collect()
        results += fail(PremiumReportFailure.GENERATION_FAILED)
        viewModel.requestComprehensiveReport()
        results += { Result.success(pdf) }
        viewModel.retryComprehensiveReport()
        assertEquals(2, calls.size)
        assertEquals(calls[0].jobId, calls[1].jobId)
        assertSame(calls[0].model, calls[1].model)
        assertFalse(calls[1].forceTokenRefresh)
        assertNull(state.comprehensiveReportMessage)
        job.cancel()
    }

    @Test
    fun `retry is a no-op when the message has no retry`() = runTest {
        val job = collect()
        assertOutcome(PremiumReportFailure.STUDY_NOT_FOUND, R.string.premium_report_study_expired, false)
        results += fail(PremiumReportFailure.STUDY_NOT_FOUND)
        viewModel.requestComprehensiveReport()
        viewModel.retryComprehensiveReport()
        assertEquals(2, calls.size)
        job.cancel()
    }

    @Test
    fun `a jobId change clears the message and the pending retry`() = runTest {
        val job = collect()
        results += fail(PremiumReportFailure.GENERATION_FAILED)
        viewModel.requestComprehensiveReport()
        sessionFlow.value = session(jobId = "job-2")
        assertNull(state.comprehensiveReportMessage)
        viewModel.retryComprehensiveReport()
        assertEquals(1, calls.size)
        job.cancel()
    }

    @Test
    fun `Basic export is independent and untouched`() = runTest {
        val job = collect()
        val gate = CompletableDeferred<Result<ByteArray>>()
        results += { gate.await() }
        viewModel.requestComprehensiveReport()
        assertTrue(state.isGeneratingComprehensiveReport)
        assertFalse(state.isGeneratingPdf)
        viewModel.exportVisibleProposalsAsPdf()
        assertEquals(1, basicCalls)
        verify(exactly = 1) { fileStore.store(byteArrayOf(9), "comparador") }
        verify(exactly = 0) { fileStore.store(any(), "informe_completo") }
        gate.complete(Result.success(pdf))
        job.cancel()
    }
}
