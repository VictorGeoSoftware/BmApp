package com.briel.marnisos.brielapp.domain.repository

import com.briel.marnisos.brielapp.domain.models.ReportAccessModel
import kotlinx.coroutines.flow.StateFlow

/**
 * Single source of truth for the broker's report access. In memory only and scoped
 * to the current user: it starts hidden, and every failure leaves it hidden.
 */
interface ReportAccessRepository {
    val access: StateFlow<ReportAccessModel>

    /** Re-checks the backend, at most once per minute per user (the first check is immediate). */
    suspend fun refresh()

    /** Resets to hidden and discards any in-flight result (logout / account switch). */
    fun clear()
}
