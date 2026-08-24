package com.briel.marnisos.brielapp.ui.views.fetchconsumption

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.briel.marnisos.brielapp.domain.models.ConsumptionStudyFailure
import com.briel.marnisos.brielapp.domain.models.ConsumptionStudyStateModel
import com.briel.marnisos.brielapp.domain.monitoring.AnalyticsEvent
import com.briel.marnisos.brielapp.domain.monitoring.AnalyticsFailureReason
import com.briel.marnisos.brielapp.domain.monitoring.AnalyticsTracker
import com.briel.marnisos.brielapp.domain.repository.ConsumptionStudyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class FetchConsumptionViewModel(
    private val consumptionStudyRepository: ConsumptionStudyRepository,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {

    private var studyStartedAtMillis: Long? = null

    init {
        observeStudyOutcome()
    }

    val uiState: StateFlow<FetchConsumptionUiState> = consumptionStudyRepository.studyState
        .map { studyState -> studyState.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = consumptionStudyRepository.studyState.value.toUiState(),
        )

    fun onPdfSelected(pdfFile: File) {
        trackStudyStarted()
        consumptionStudyRepository.startFromPdf(pdfFile)
    }

    fun onCupsConfirmed(cupsCode: String) {
        trackStudyStarted()
        consumptionStudyRepository.startFromCups(cupsCode)
    }

    fun onFailureShown() {
        consumptionStudyRepository.consumeFailure()
    }

    private fun ConsumptionStudyStateModel.toUiState(): FetchConsumptionUiState = when (this) {
        is ConsumptionStudyStateModel.Idle -> FetchConsumptionUiState()
        is ConsumptionStudyStateModel.InProgress -> FetchConsumptionUiState(
            isStudyRunning = true,
            step = step,
        )

        is ConsumptionStudyStateModel.Failed -> FetchConsumptionUiState(failure = cause)
    }

    private fun trackStudyStarted() {
        studyStartedAtMillis = System.currentTimeMillis()
        analyticsTracker.track(AnalyticsEvent.ConsumptionFetchStarted)
    }

    /**
     * The study runs in the data layer and outlives this screen, so the outcome is
     * only observable as a state transition. Success is `InProgress -> Idle`;
     * `InProgress -> Failed` is the failure. Transitions that do not start from
     * `InProgress` (initial emission, restoration) are ignored so a returning user
     * does not re-report a finished study.
     */
    private fun observeStudyOutcome() {
        viewModelScope.launch {
            consumptionStudyRepository.studyState
                .runningFold(
                    initial = null as Pair<ConsumptionStudyStateModel, ConsumptionStudyStateModel>?,
                ) { accumulator, next ->
                    val previous = accumulator?.second ?: return@runningFold next to next
                    previous to next
                }
                .collect { transition ->
                    val (previous, next) = transition ?: return@collect
                    if (previous !is ConsumptionStudyStateModel.InProgress) return@collect

                    when (next) {
                        is ConsumptionStudyStateModel.Idle -> analyticsTracker.track(
                            AnalyticsEvent.ConsumptionFetchSucceeded(durationMs = elapsedMillis()),
                        )

                        is ConsumptionStudyStateModel.Failed -> analyticsTracker.track(
                            AnalyticsEvent.ConsumptionFetchFailed(next.cause.toAnalyticsReason()),
                        )

                        is ConsumptionStudyStateModel.InProgress -> Unit
                    }
                }
        }
    }

    private fun elapsedMillis(): Long =
        studyStartedAtMillis?.let { System.currentTimeMillis() - it }?.coerceAtLeast(0L) ?: 0L

    private fun ConsumptionStudyFailure.toAnalyticsReason(): AnalyticsFailureReason = when (this) {
        ConsumptionStudyFailure.SUBMIT_FAILED -> AnalyticsFailureReason.NETWORK
        ConsumptionStudyFailure.PROCESSING_FAILED -> AnalyticsFailureReason.PARSE_ERROR
        ConsumptionStudyFailure.STATUS_CHECK_FAILED -> AnalyticsFailureReason.NETWORK
        ConsumptionStudyFailure.RESULT_FETCH_FAILED -> AnalyticsFailureReason.NETWORK
        ConsumptionStudyFailure.TIMEOUT -> AnalyticsFailureReason.TIMEOUT
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
