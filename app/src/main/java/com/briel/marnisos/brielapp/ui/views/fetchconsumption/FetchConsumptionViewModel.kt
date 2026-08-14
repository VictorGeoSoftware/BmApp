package com.briel.marnisos.brielapp.ui.views.fetchconsumption

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.briel.marnisos.brielapp.domain.models.ConsumptionStudyStateModel
import com.briel.marnisos.brielapp.domain.repository.ConsumptionStudyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.io.File

class FetchConsumptionViewModel(
    private val consumptionStudyRepository: ConsumptionStudyRepository,
) : ViewModel() {

    val uiState: StateFlow<FetchConsumptionUiState> = consumptionStudyRepository.studyState
        .map { studyState -> studyState.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = consumptionStudyRepository.studyState.value.toUiState(),
        )

    fun onPdfSelected(pdfFile: File) {
        consumptionStudyRepository.startFromPdf(pdfFile)
    }

    fun onCupsConfirmed(cupsCode: String) {
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

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
