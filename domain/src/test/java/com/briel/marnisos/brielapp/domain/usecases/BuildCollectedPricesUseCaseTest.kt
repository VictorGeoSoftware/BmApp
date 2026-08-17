package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.ConsumptionSessionModel
import com.briel.marnisos.brielapp.domain.models.CurrentUserConditionsModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class BuildCollectedPricesUseCaseTest {

    private val buildCollectedPrices = BuildCollectedPricesUseCase.Factory.create()

    @Test
    fun `builds a payload from the session periods and entered prices`() {
        val collected = buildCollectedPrices(session(), completeConditions())

        assertNotNull(collected)
        assertEquals("Iberdrola", collected!!.companyName)
        assertEquals("3.0TD", collected.tariffName)
        assertEquals(mapOf("P1" to 0.101664, "P2" to 0.015864), collected.powerTermPriceByPeriod)
        assertEquals(
            mapOf("P1" to 0.1452, "P2" to 0.13287, "P3" to 0.09845),
            collected.energyPriceByPeriod,
        )
        assertEquals(25.0, collected.extraServices)
    }

    @Test
    fun `parses comma and dot decimal separators alike`() {
        val collected = buildCollectedPrices(
            session(powerPeriods = listOf("P1", "P2"), energyPeriods = emptyList()),
            conditions(powerTerm = mapOf("P1" to "0,5", "P2" to "0.25")),
        )

        assertEquals(mapOf("P1" to 0.5, "P2" to 0.25), collected!!.powerTermPriceByPeriod)
    }

    @Test
    fun `trims the supplier name`() {
        val collected = buildCollectedPrices(
            session(),
            completeConditions(companyName = "  Endesa  "),
        )

        assertEquals("Endesa", collected!!.companyName)
    }

    @Test
    fun `only includes periods that belong to the study`() {
        val collected = buildCollectedPrices(
            session(powerPeriods = listOf("P1"), energyPeriods = listOf("P1")),
            conditions(
                powerTerm = mapOf("P1" to "1.0", "P6" to "9.99"),
                energy = mapOf("P1" to "0.5", "P5" to "9.99"),
            ),
        )

        assertEquals(setOf("P1"), collected!!.powerTermPriceByPeriod.keys)
        assertEquals(setOf("P1"), collected.energyPriceByPeriod.keys)
    }

    @Test
    fun `skips periods whose price cannot be parsed`() {
        val collected = buildCollectedPrices(
            session(powerPeriods = listOf("P1", "P2"), energyPeriods = emptyList()),
            conditions(powerTerm = mapOf("P1" to "1.0", "P2" to "not a number")),
        )

        assertEquals(mapOf("P1" to 1.0), collected!!.powerTermPriceByPeriod)
    }

    @Test
    fun `leaves extra services null when it is blank or unparseable`() {
        val collected = buildCollectedPrices(session(), completeConditions(extraServices = ""))

        assertNull(collected!!.extraServices)
    }

    @Test
    fun `returns null when there is no session`() {
        assertNull(buildCollectedPrices(null, completeConditions()))
    }

    @Test
    fun `returns null when there are no conditions`() {
        assertNull(buildCollectedPrices(session(), null))
    }

    @Test
    fun `returns null when the supplier is missing`() {
        assertNull(buildCollectedPrices(session(), completeConditions(companyName = "   ")))
    }

    @Test
    fun `returns null when no price could be parsed at all`() {
        assertNull(
            buildCollectedPrices(
                session(),
                conditions(powerTerm = mapOf("P1" to ""), energy = mapOf("P1" to "abc")),
            ),
        )
    }

    private fun session(
        tariffName: String = "3.0TD",
        powerPeriods: List<String> = listOf("P1", "P2"),
        energyPeriods: List<String> = listOf("P1", "P2", "P3"),
    ) = ConsumptionSessionModel(
        jobId = "job-1",
        tariffName = tariffName,
        ivaPercent = 21.0,
        electricTaxPercent = 5.11,
        powerTermRows = powerPeriods.map { period -> period to 4.4 },
        energyConsumedRows = energyPeriods.map { period -> period to 1200 },
        supplyHolder = "Cliente",
        supplyAddress = "Calle",
        supplyCupsCode = "ES0021000006543210XY",
        proposals = emptyList(),
    )

    private fun conditions(
        companyName: String = "Iberdrola",
        powerTerm: Map<String, String> = emptyMap(),
        energy: Map<String, String> = emptyMap(),
        extraServices: String = "",
    ) = CurrentUserConditionsModel(
        companyName = companyName,
        powerTermPriceByPeriod = powerTerm,
        energyPriceByPeriod = energy,
        extraServices = extraServices,
    )

    private fun completeConditions(
        companyName: String = "Iberdrola",
        extraServices: String = "25.0",
    ) = conditions(
        companyName = companyName,
        powerTerm = mapOf("P1" to "0,101664", "P2" to "0,015864"),
        energy = mapOf("P1" to "0,145200", "P2" to "0,132870", "P3" to "0,098450"),
        extraServices = extraServices,
    )
}
