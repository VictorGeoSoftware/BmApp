package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.ConsumptionSessionModel
import com.briel.marnisos.brielapp.domain.models.CurrentUserConditionsModel
import com.briel.marnisos.brielapp.domain.models.FeeFirstStage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EvaluateFeeFirstGateUseCaseTest {

    private val evaluateFeeFirstGate = EvaluateFeeFirstGateUseCase.Factory.create()

    @Test
    fun `returns consumption required when there is no session`() {
        val gate = evaluateFeeFirstGate(null, conditions())

        assertEquals(FeeFirstStage.CONSUMPTION_REQUIRED, gate.stage)
        assertFalse(gate.hasFetchedConsumption)
        assertFalse(gate.isUnlocked)
    }

    @Test
    fun `returns current conditions required when no price has been entered`() {
        val gate = evaluateFeeFirstGate(session(), currentUserConditions = null)

        assertEquals(FeeFirstStage.CURRENT_CONDITIONS_REQUIRED, gate.stage)
        assertTrue(gate.hasFetchedConsumption)
        assertEquals(5, gate.requiredFieldCount)
        assertEquals(0, gate.completedRequiredFieldCount)
    }

    @Test
    fun `stays locked while any required period is missing`() {
        val gate = evaluateFeeFirstGate(
            session(),
            conditions(
                powerTerm = mapOf("P1" to "0,101664", "P2" to "0,015864"),
                energy = mapOf("P1" to "0,145200"),
            ),
        )

        assertEquals(FeeFirstStage.CURRENT_CONDITIONS_REQUIRED, gate.stage)
        assertEquals(3, gate.completedRequiredFieldCount)
        assertEquals(5, gate.requiredFieldCount)
    }

    @Test
    fun `does not count blank or zero values`() {
        val gate = evaluateFeeFirstGate(
            session(),
            conditions(
                powerTerm = mapOf("P1" to "0,101664", "P2" to "   "),
                energy = mapOf("P1" to "0", "P2" to "0,00", "P3" to "not a number"),
            ),
        )

        assertEquals(1, gate.completedRequiredFieldCount)
        assertFalse(gate.areCurrentConditionsComplete)
    }

    @Test
    fun `accepts both comma and dot decimal separators`() {
        val gate = evaluateFeeFirstGate(
            session(),
            conditions(
                powerTerm = mapOf("P1" to "0,101664", "P2" to "0.015864"),
                energy = mapOf("P1" to "0.145200", "P2" to "0,132870", "P3" to "0.098450"),
            ),
        )

        assertEquals(FeeFirstStage.UNLOCKED, gate.stage)
        assertTrue(gate.isUnlocked)
        assertEquals(5, gate.completedRequiredFieldCount)
    }

    @Test
    fun `unlocks even when extra services is empty`() {
        val gate = evaluateFeeFirstGate(session(), completeConditions(extraServices = ""))

        assertEquals(FeeFirstStage.UNLOCKED, gate.stage)
    }

    @Test
    fun `ignores prices for periods that are not part of the study`() {
        val gate = evaluateFeeFirstGate(
            session(powerPeriods = listOf("P1"), energyPeriods = listOf("P1")),
            conditions(
                powerTerm = mapOf("P1" to "0,101664", "P6" to "9,99"),
                energy = mapOf("P1" to "0,145200", "P5" to "9,99"),
            ),
        )

        assertEquals(FeeFirstStage.UNLOCKED, gate.stage)
        assertEquals(2, gate.requiredFieldCount)
        assertEquals(2, gate.completedRequiredFieldCount)
    }

    private fun session(
        powerPeriods: List<String> = listOf("P1", "P2"),
        energyPeriods: List<String> = listOf("P1", "P2", "P3"),
    ) = ConsumptionSessionModel(
        jobId = "job-1",
        tariffName = "2.0TD",
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
        powerTerm: Map<String, String> = emptyMap(),
        energy: Map<String, String> = emptyMap(),
        extraServices: String = "",
    ) = CurrentUserConditionsModel(
        powerTermPriceByPeriod = powerTerm,
        energyPriceByPeriod = energy,
        extraServices = extraServices,
    )

    private fun completeConditions(extraServices: String) = conditions(
        powerTerm = mapOf("P1" to "0,101664", "P2" to "0,015864"),
        energy = mapOf("P1" to "0,145200", "P2" to "0,132870", "P3" to "0,098450"),
        extraServices = extraServices,
    )
}
