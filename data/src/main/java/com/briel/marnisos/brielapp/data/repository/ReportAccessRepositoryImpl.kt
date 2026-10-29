package com.briel.marnisos.brielapp.data.repository

import com.briel.marnisos.brielapp.data.network.UserAccessApi
import com.briel.marnisos.brielapp.data.utils.ReportAccessMapper
import com.briel.marnisos.brielapp.domain.models.ReportAccessModel
import com.briel.marnisos.brielapp.domain.repository.AuthRepository
import com.briel.marnisos.brielapp.domain.repository.ReportAccessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory, uid-scoped report access. Fails hidden: no user, no token, any non-200,
 * network error or malformed body all leave the action hidden, and none signs out.
 * Results for a uid that is no longer current, or from a superseded request, are dropped.
 */
class ReportAccessRepositoryImpl(
    private val authRepository: AuthRepository,
    private val userAccessApi: UserAccessApi,
    private val clock: () -> Long = System::currentTimeMillis,
) : ReportAccessRepository {

    private val lock = Any()
    private val _access = MutableStateFlow(ReportAccessModel())
    override val access: StateFlow<ReportAccessModel> = _access.asStateFlow()

    private var currentUid: String? = null
    private var lastCheckedAt: Long? = null
    private var requestCounter = 0L

    override suspend fun refresh() {
        val uid = authRepository.getCurrentUser()?.uid
        if (uid == null) {
            clear()
            return
        }

        val requestId = synchronized(lock) {
            if (uid != currentUid) {
                resetLocked()
                currentUid = uid
            }
            val now = clock()
            val last = lastCheckedAt
            if (last != null && now - last < THROTTLE_MILLIS) return
            lastCheckedAt = now
            ++requestCounter
        }

        val token = authRepository.getIdToken(false).getOrNull()
        if (token.isNullOrBlank()) {
            synchronized(lock) {
                if (isCurrentLocked(requestId, uid)) {
                    _access.value = ReportAccessModel()
                    // No request was made: let the next trigger try again straight away.
                    lastCheckedAt = null
                }
            }
            return
        }

        val allowed = userAccessApi.fetchAccess(token)
            .map(ReportAccessMapper::canRequestComprehensiveReport)
            .getOrDefault(false)

        synchronized(lock) {
            if (isCurrentLocked(requestId, uid) && authRepository.getCurrentUser()?.uid == uid) {
                _access.value = ReportAccessModel(canRequestComprehensiveReport = allowed)
            }
        }
    }

    override fun clear() {
        synchronized(lock) { resetLocked() }
    }

    private fun resetLocked() {
        requestCounter++
        currentUid = null
        lastCheckedAt = null
        _access.value = ReportAccessModel()
    }

    private fun isCurrentLocked(requestId: Long, uid: String): Boolean =
        requestId == requestCounter && uid == currentUid

    private companion object {
        const val THROTTLE_MILLIS = 60_000L
    }
}
