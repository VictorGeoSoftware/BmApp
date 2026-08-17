package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.ComparatorSummaryModel

/**
 * Picks the proposals that are so much more expensive than the customer's current
 * contract that showing them only adds noise to the comparison.
 *
 * A proposal is uncompetitive when it costs at least [thresholdPercent] more per year
 * than the customer pays today. Savings are expressed as a positive percentage when the
 * proposal is cheaper, so the test is against the negated threshold.
 *
 * The cheapest proposal is never hidden when *every* proposal is uncompetitive: a
 * customer already on very good prices would otherwise land on an empty comparison that
 * reads as "the study returned nothing". Showing the closest option keeps the screen
 * meaningful and leaves the report exportable. It is not presented as a saving, because
 * [com.briel.marnisos.brielapp.domain.models.ComparatorSummaryModel.bestProposalAmong]
 * only considers proposals that actually beat the customer.
 *
 * This is a pure selection: applying the result is the repository's job, so the rule can
 * be unit tested and reused without touching visibility state.
 */
class SelectUncompetitiveProposalsUseCase {

    operator fun invoke(
        summary: ComparatorSummaryModel,
        thresholdPercent: Int = DEFAULT_THRESHOLD_PERCENT,
    ): Set<String> {
        // Without the customer's prices there is nothing to compare against, and hiding
        // everything would look like the study came back empty.
        if (summary.customerTotalAnnualPrice <= 0.0) return emptySet()

        val uncompetitiveTitles = summary.proposals
            .map { proposal -> proposal.proposalTitle }
            .filter { title ->
                val savingsPercentage = summary.annualSavingsPercentageByTitle[title] ?: return@filter false
                savingsPercentage <= -thresholdPercent
            }
            .toSet()

        if (uncompetitiveTitles.size < summary.proposals.size) return uncompetitiveTitles

        val cheapestTitle = summary.proposals
            .minByOrNull { proposal -> proposal.totalAnnualPrice }
            ?.proposalTitle

        return uncompetitiveTitles - setOfNotNull(cheapestTitle)
    }

    companion object {
        const val DEFAULT_THRESHOLD_PERCENT = 15
    }
}
