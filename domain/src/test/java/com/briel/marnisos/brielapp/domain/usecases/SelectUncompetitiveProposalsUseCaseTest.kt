package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.ComparatorSummaryModel
import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SelectUncompetitiveProposalsUseCaseTest {

    private val useCase = SelectUncompetitiveProposalsUseCase()

    @Test
    fun `hides proposals at or beyond the threshold`() {
        val summary = summaryOf(
            "Cheaper" to 20,
            "Slightly worse" to -14,
            "Exactly at the limit" to -15,
            "Much worse" to -40,
        )

        assertEquals(
            setOf("Exactly at the limit", "Much worse"),
            useCase(summary),
        )
    }

    @Test
    fun `keeps a proposal one point under the threshold`() {
        val summary = summaryOf("Borderline" to -14)

        assertTrue(useCase(summary).isEmpty())
    }

    @Test
    fun `hides nothing when the customer has no prices yet`() {
        val summary = summaryOf("Much worse" to -80).copy(customerTotalAnnualPrice = 0.0)

        assertTrue(useCase(summary).isEmpty())
    }

    @Test
    fun `ignores proposals with no comparison available`() {
        val summary = ComparatorSummaryModel(
            proposals = listOf(proposal("Unknown", totalAnnualPrice = 1_000.0)),
            customerTotalAnnualPrice = 1_000.0,
            annualSavingsPercentageByTitle = emptyMap(),
        )

        assertTrue(useCase(summary).isEmpty())
    }

    @Test
    fun `threshold is configurable`() {
        val summary = summaryOf("Worse by twenty" to -20, "Cheaper" to 20)

        assertTrue(useCase(summary, thresholdPercent = 25).isEmpty())
        assertEquals(setOf("Worse by twenty"), useCase(summary, thresholdPercent = 20))
    }

    @Test
    fun `keeps the cheapest proposal when every proposal is uncompetitive`() {
        val summary = summaryOf(
            "Closest" to -20,
            "Worse" to -40,
            "Hopeless" to -80,
        )

        assertEquals(setOf("Worse", "Hopeless"), useCase(summary))
    }

    @Test
    fun `keeps the only proposal when it is uncompetitive`() {
        val summary = summaryOf("Only option" to -60)

        assertTrue(useCase(summary).isEmpty())
    }

    /**
     * The retained proposal is chosen by total annual price rather than by savings
     * percentage, which is rounded to an int and can therefore tie.
     */
    @Test
    fun `retains by total annual price when savings percentages tie`() {
        val summary = ComparatorSummaryModel(
            proposals = listOf(
                proposal("Pricier", totalAnnualPrice = 1_260.0),
                proposal("Cheaper", totalAnnualPrice = 1_255.0),
            ),
            customerTotalAnnualPrice = 1_000.0,
            annualSavingsPercentageByTitle = mapOf("Pricier" to -26, "Cheaper" to -26),
        )

        assertEquals(setOf("Pricier"), useCase(summary))
    }

    /**
     * Builds a summary whose prices agree with the savings percentages, exactly as
     * [CalculateComparatorSummaryUseCase] produces them: a proposal saving 20% costs
     * 20% less than the customer's total, one at -40% costs 40% more. Keeping the two
     * consistent is what makes "cheapest" meaningful in these tests.
     */
    private fun summaryOf(vararg savingsByTitle: Pair<String, Int>) = ComparatorSummaryModel(
        proposals = savingsByTitle.map { (title, savingsPercentage) ->
            proposal(
                proposalTitle = title,
                totalAnnualPrice = CUSTOMER_TOTAL * (1.0 - savingsPercentage / 100.0),
            )
        },
        customerTotalAnnualPrice = CUSTOMER_TOTAL,
        annualSavingsPercentageByTitle = savingsByTitle.toMap(),
    )

    private fun proposal(proposalTitle: String, totalAnnualPrice: Double) = ProposalPriceModel(
        proposalTitle = proposalTitle,
        powerTermItems = emptyList(),
        annualPowerTermCost = 0.0,
        consumedEnergyItems = emptyList(),
        annualEnergyCost = 0.0,
        extraServices = 0.0,
        iva = 0.0,
        electricalTax = 0.0,
        totalAnnualPrice = totalAnnualPrice,
    )

    private companion object {
        const val CUSTOMER_TOTAL = 1_000.0
    }
}
