package com.briel.marnisos.brielapp.domain.repository

import com.briel.marnisos.brielapp.domain.models.ConsumptionReportModel
import com.briel.marnisos.brielapp.domain.models.ConsumptionSessionModel
import kotlinx.coroutines.flow.StateFlow

/**
 * Single source of truth for the active consumption study and the per-proposal
 * overrides applied on top of it.
 *
 * Screen ViewModels observe this repository instead of sharing a ViewModel.
 */
interface ConsumptionSessionRepository {

    val session: StateFlow<ConsumptionSessionModel?>

    val proposalVisibilityByTitle: StateFlow<Map<String, Boolean>>

    val proposalFixedAmountByTitle: StateFlow<Map<String, String>>

    fun updateFromReport(jobId: String, report: ConsumptionReportModel)

    fun updateSupplyHolder(supplyHolder: String)

    fun updateSupplyAddress(supplyAddress: String)

    fun setProposalVisibility(proposalTitle: String, isVisible: Boolean)

    fun setProposalFixedAmount(proposalTitle: String, fixedAmountInput: String)

    fun clear()
}
