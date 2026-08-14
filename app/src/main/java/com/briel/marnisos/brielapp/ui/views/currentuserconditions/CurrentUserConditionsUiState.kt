package com.briel.marnisos.brielapp.ui.views.currentuserconditions

import com.briel.marnisos.brielapp.domain.models.FeeFirstGateModel
import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel

/**
 * State of the current-conditions screen: the fee-first gate, the customer's supply
 * data and the editable price rows.
 */
data class CurrentUserConditionsUiState(
    val gate: FeeFirstGateModel = FeeFirstGateModel(),
    val form: CurrentUserConditionsFormState = CurrentUserConditionsFormState(),
    val supplyHolder: String = "",
    val supplyAddress: String = "",
    val supplyCupsCode: String = "",
    val availableProposals: List<ProposalPriceModel> = emptyList(),
) {
    val hasFetchedConsumption: Boolean get() = gate.hasFetchedConsumption
    val canCopyFromProposal: Boolean get() = availableProposals.isNotEmpty()
}
