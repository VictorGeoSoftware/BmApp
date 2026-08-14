package com.briel.marnisos.brielapp.ui.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.briel.marnisos.brielapp.domain.repository.ConsumptionStudyRepository
import com.briel.marnisos.brielapp.domain.repository.PriceUpdatesNotifier
import com.briel.marnisos.brielapp.domain.usecases.ObserveFeeFirstGateUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Owns the shell-level state: the fee-first stage that gates navigation, and the
 * running-study flag shown in the top bar.
 */
class AppShellViewModel(
    private val consumptionStudyRepository: ConsumptionStudyRepository,
    priceUpdatesNotifier: PriceUpdatesNotifier,
    observeFeeFirstGateUseCase: ObserveFeeFirstGateUseCase,
) : ViewModel() {

    val uiState: StateFlow<AppShellUiState> = combine(
        observeFeeFirstGateUseCase(),
        consumptionStudyRepository.studyState,
    ) { gate, studyState ->
        AppShellUiState(
            stage = gate.stage,
            isStudyRunning = studyState.isRunning,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = AppShellUiState(),
    )

    init {
        viewModelScope.launch {
            consumptionStudyRepository.restoreLastCompletedStudy()
        }

        viewModelScope.launch {
            priceUpdatesNotifier.events.collect {
                consumptionStudyRepository.refreshActiveStudy()
            }
        }
    }

    /**
     * Rule R6: starting a new study discards the active one and every trace of the
     * previous customer, including the persisted job. Navigation follows from the
     * stage change alone — the NavHost redirects to the fetch step.
     */
    fun startNewStudy() {
        viewModelScope.launch {
            consumptionStudyRepository.discardActiveStudy()
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
