package com.briel.marnisos.brielapp.ui.views.proposals

import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel
import com.briel.marnisos.brielapp.ui.views.comparator.customerconditions.CustomerConditionsUiState

/**
 * State of the proposals comparison screen.
 */
data class ProposalsUiState(
    val tariffName: String = "",
    val powerTermRows: List<Pair<String, Double>> = emptyList(),
    val energyConsumedRows: List<Pair<String, Int>> = emptyList(),
    val ivaLabel: String = "",
    val electricTaxLabel: String = "",
    val visibleProposals: List<ProposalPriceModel> = emptyList(),
    val annualPriceDeltaByTitle: Map<String, Double> = emptyMap(),
    val annualSavingsPercentageByTitle: Map<String, Int> = emptyMap(),
    val fixedAmountByTitle: Map<String, String> = emptyMap(),
    val customerConditions: CustomerConditionsUiState = CustomerConditionsUiState(),
    val bestProposalTitle: String? = null,
    val bestProposalAnnualSaving: String? = null,
    val isGeneratingPdf: Boolean = false,
) {
    val hasProposals: Boolean get() = visibleProposals.isNotEmpty()
}
