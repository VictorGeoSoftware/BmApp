package com.briel.marnisos.brielapp.domain.models

/**
 * Everything the comparator needs once the customer's prices and the active study
 * have been combined: recalculated proposals, the customer column, and the savings
 * of each proposal against it.
 */
data class ComparatorSummaryModel(
    val proposals: List<ProposalPriceModel> = emptyList(),
    val customerConditions: CustomerConditionsColumnModel = CustomerConditionsColumnModel(),
    val customerTotalAnnualPrice: Double = 0.0,
    val annualPriceDeltaByTitle: Map<String, Double> = emptyMap(),
    val annualSavingsPercentageByTitle: Map<String, Int> = emptyMap(),
) {
    /** Cheapest proposal among [candidates], or null when nothing is comparable yet. */
    fun bestProposalAmong(candidates: List<ProposalPriceModel>): ProposalPriceModel? {
        if (customerTotalAnnualPrice <= 0.0) return null
        return candidates
            .filter { proposal -> (annualPriceDeltaByTitle[proposal.proposalTitle] ?: 0.0) > 0.0 }
            .minByOrNull { proposal -> proposal.totalAnnualPrice }
    }
}
