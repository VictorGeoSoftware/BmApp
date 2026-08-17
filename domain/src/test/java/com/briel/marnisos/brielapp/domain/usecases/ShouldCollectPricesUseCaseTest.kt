package com.briel.marnisos.brielapp.domain.usecases

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShouldCollectPricesUseCaseTest {

    private val shouldCollectPrices = ShouldCollectPricesUseCase.Factory.create()

    @Test
    fun `does not collect 2 0TD however it is written`() {
        listOf("2.0TD", "2.0 TD", " 2.0td ", "2-0-TD", "20TD", "2.0Td").forEach { tariff ->
            assertFalse("expected '$tariff' to be excluded", shouldCollectPrices(tariff))
        }
    }

    @Test
    fun `collects every other tariff`() {
        listOf("3.0TD", "3.1TD", "6.1TD", "3.0 td", "6.2TD").forEach { tariff ->
            assertTrue("expected '$tariff' to be collected", shouldCollectPrices(tariff))
        }
    }

    @Test
    fun `does not collect when the tariff is unknown`() {
        assertFalse(shouldCollectPrices(null))
        assertFalse(shouldCollectPrices(""))
        assertFalse(shouldCollectPrices("   "))
        assertFalse(shouldCollectPrices("-.-"))
    }
}
