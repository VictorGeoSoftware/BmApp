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

    /**
     * Hides [proposalTitles] exactly as the Configuration screen would, but only the
     * first time it is called for [signature].
     *
     * The one-shot guard is what makes the automatic hiding safe: the broker can
     * re-enable any of those proposals in Configuration and the choice sticks, because
     * the rule will not run again for the same study and the same customer prices.
     * A new signature (new study, or edited prices) means the comparison changed, so
     * re-applying is the correct behaviour.
     */
    fun hideProposalsOnce(signature: String, proposalTitles: Set<String>)

    /**
     * Marks [jobId] as having had its collected prices submitted, returning `true` only
     * the first time it is called for that id.
     *
     * The guard lives here rather than in a ViewModel because the broker can leave and
     * re-enter the current-conditions screen to fix a mistyped price: that destroys the
     * screen's ViewModel, so a ViewModel-local guard would forget and submit the same
     * customer twice. The collected-prices table is append-only, so a duplicate cannot
     * be corrected afterwards.
     */
    fun markCollectedPricesSubmitted(jobId: String): Boolean

    fun clear()
}
