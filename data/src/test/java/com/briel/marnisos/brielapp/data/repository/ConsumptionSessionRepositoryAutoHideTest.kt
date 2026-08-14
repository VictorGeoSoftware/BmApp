package com.briel.marnisos.brielapp.data.repository

import com.briel.marnisos.brielapp.domain.models.CleanedConsumptionDataModel
import com.briel.marnisos.brielapp.domain.models.ConsumptionReportModel
import com.briel.marnisos.brielapp.domain.models.DoclingExtractedDataModel
import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Covers the guard behind the automatic hiding of uncompetitive proposals: it must run
 * once per signature, and must never override a choice the broker made afterwards.
 */
class ConsumptionSessionRepositoryAutoHideTest {

    private val repository = ConsumptionSessionRepositoryImpl()

    @Test
    fun `hides the given proposals`() {
        repository.loadStudyWith("Cheap", "Expensive")

        repository.hideProposalsOnce(SIGNATURE, setOf("Expensive"))

        assertEquals(
            mapOf("Cheap" to true, "Expensive" to false),
            repository.proposalVisibilityByTitle.value,
        )
    }

    @Test
    fun `does not run twice for the same signature`() {
        repository.loadStudyWith("Cheap", "Expensive")
        repository.hideProposalsOnce(SIGNATURE, setOf("Expensive"))

        // The broker changes their mind in Configuration.
        repository.setProposalVisibility("Expensive", isVisible = true)
        repository.hideProposalsOnce(SIGNATURE, setOf("Expensive"))

        assertEquals(true, repository.proposalVisibilityByTitle.value["Expensive"])
    }

    @Test
    fun `runs again when the comparison changes`() {
        repository.loadStudyWith("Cheap", "Expensive")
        repository.hideProposalsOnce(SIGNATURE, setOf("Expensive"))
        repository.setProposalVisibility("Expensive", isVisible = true)

        repository.hideProposalsOnce("job-1@2000.0", setOf("Expensive"))

        assertEquals(false, repository.proposalVisibilityByTitle.value["Expensive"])
    }

    @Test
    fun `ignores titles the study did not return`() {
        repository.loadStudyWith("Cheap")

        repository.hideProposalsOnce(SIGNATURE, setOf("Not in this study"))

        assertEquals(mapOf("Cheap" to true), repository.proposalVisibilityByTitle.value)
    }

    @Test
    fun `a new study re-arms the guard`() {
        repository.loadStudyWith("Cheap", "Expensive")
        repository.hideProposalsOnce(SIGNATURE, setOf("Expensive"))

        repository.clear()
        repository.loadStudyWith("Cheap", "Expensive")
        repository.hideProposalsOnce(SIGNATURE, setOf("Expensive"))

        assertEquals(false, repository.proposalVisibilityByTitle.value["Expensive"])
    }

    private fun ConsumptionSessionRepositoryImpl.loadStudyWith(vararg titles: String) {
        updateFromReport(jobId = "job-1", report = reportWith(titles.toList()))
    }

    private fun reportWith(titles: List<String>) = ConsumptionReportModel(
        success = true,
        userData = DoclingExtractedDataModel(cupsCode = "ES0031104740917002MS"),
        consumptionData = consumptionData(),
        proposals = titles.map { title -> proposal(title) },
        iva = 21.0,
        impuestoElectrico = 5.11,
    )

    private fun consumptionData() = CleanedConsumptionDataModel(
        cups = "ES0031104740917002MS",
        tarifa = "2.0TD",
        tarifaValue = "2.0TD",
        annualConsumption = 3_500.0,
        annualConsumptionP1 = 1_000.0,
        annualConsumptionP2 = 1_000.0,
        annualConsumptionP3 = 1_500.0,
        annualConsumptionP4 = 0.0,
        annualConsumptionP5 = 0.0,
        annualConsumptionP6 = 0.0,
        subscribedPowerP1 = 4.6,
        subscribedPowerP2 = 4.6,
        subscribedPowerP3 = 0.0,
        subscribedPowerP4 = 0.0,
        subscribedPowerP5 = 0.0,
        subscribedPowerP6 = 0.0,
        feeType = "2.0TD",
        fileName = "bill.pdf",
        processedAt = "2026-08-14T00:00:00Z",
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

    private companion object {
        const val SIGNATURE = "job-1@1000.0"
    }
}
