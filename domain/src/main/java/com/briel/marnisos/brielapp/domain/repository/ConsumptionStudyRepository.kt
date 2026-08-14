package com.briel.marnisos.brielapp.domain.repository

import com.briel.marnisos.brielapp.domain.models.ConsumptionStudyStateModel
import kotlinx.coroutines.flow.StateFlow
import java.io.File

/**
 * Runs consumption studies (PDF upload / CUPS lookup) and publishes the result into
 * [ConsumptionSessionRepository].
 *
 * Implementations are application-scoped: an in-flight study survives navigation and
 * screen recreation.
 */
interface ConsumptionStudyRepository {

    val studyState: StateFlow<ConsumptionStudyStateModel>

    fun startFromPdf(pdfFile: File)

    fun startFromCups(cupsCode: String)

    /** Restores the last completed study, if the persisted job is still available. */
    suspend fun restoreLastCompletedStudy()

    /**
     * Discards the active study and everything derived from it — the session, the
     * proposal overrides, the customer's prices and the persisted job id — so the
     * next study starts from a clean slate (rule R6).
     */
    suspend fun discardActiveStudy()

    /** Re-fetches prices for the active study after a backend price update. */
    suspend fun refreshActiveStudy()

    fun consumeFailure()
}
