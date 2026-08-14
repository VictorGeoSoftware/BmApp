package com.briel.marnisos.brielapp.ui.views.configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.briel.marnisos.brielapp.domain.repository.ConsumptionSessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ConfigurationViewModel(
    private val consumptionSessionRepository: ConsumptionSessionRepository,
) : ViewModel() {

    val uiState: StateFlow<ConfigurationUiState> = combine(
        consumptionSessionRepository.session,
        consumptionSessionRepository.proposalVisibilityByTitle,
    ) { session, visibilityByTitle ->
        ConfigurationUiState(
            proposals = session?.proposals.orEmpty(),
            visibilityByTitle = visibilityByTitle,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = ConfigurationUiState(),
    )

    fun onProposalVisibilityChanged(proposalTitle: String, isVisible: Boolean) {
        consumptionSessionRepository.setProposalVisibility(proposalTitle, isVisible)
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
