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

        return summary.proposals
            .map { proposal -> proposal.proposalTitle }
            .filter { title ->
                val savingsPercentage = summary.annualSavingsPercentageByTitle[title] ?: return@filter false
                savingsPercentage <= -thresholdPercent
            }
            .toSet()
    }

    companion object {
        const val DEFAULT_THRESHOLD_PERCENT = 15
    }
}
