package com.briel.marnisos.brielapp.ui.views.configuration

import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel

/**
 * State of the proposal-visibility configuration screen.
 */
data class ConfigurationUiState(
    val proposals: List<ProposalPriceModel> = emptyList(),
    val visibilityByTitle: Map<String, Boolean> = emptyMap(),
)
