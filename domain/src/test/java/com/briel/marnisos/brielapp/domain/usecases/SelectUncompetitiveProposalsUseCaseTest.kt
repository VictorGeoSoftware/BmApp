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
            proposals = listOf(proposal("Unknown")),
            customerTotalAnnualPrice = 1_000.0,
            annualSavingsPercentageByTitle = emptyMap(),
        )

        assertTrue(useCase(summary).isEmpty())
    }

    @Test
    fun `threshold is configurable`() {
        val summary = summaryOf("Worse by twenty" to -20)

        assertTrue(useCase(summary, thresholdPercent = 25).isEmpty())
        assertEquals(setOf("Worse by twenty"), useCase(summary, thresholdPercent = 20))
    }

    private fun summaryOf(vararg savingsByTitle: Pair<String, Int>) = ComparatorSummaryModel(
        proposals = savingsByTitle.map { (title, _) -> proposal(title) },
        customerTotalAnnualPrice = 1_000.0,
        annualSavingsPercentageByTitle = savingsByTitle.toMap(),
    )

    private fun proposal(title: String) = ProposalPriceModel(
        proposalTitle = title,
        powerTermItems = emptyList(),
        annualPowerTermCost = 0.0,
        consumedEnergyItems = emptyList(),
        annualEnergyCost = 0.0,
        extraServices = 0.0,
        iva = 0.0,
        electricalTax = 0.0,
        totalAnnualPrice = 0.0,
    )
}
