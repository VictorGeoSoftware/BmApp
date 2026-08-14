package com.briel.marnisos.brielapp.ui.views.pricetable

import org.junit.Assert.assertEquals
import org.junit.Test

class MiddleEllipsizeTest {

    @Test
    fun `keeps titles at or below the limit untouched`() {
        assertEquals("Propuesta 1", "Propuesta 1".middleEllipsize())
        assertEquals("Media indexada AB", "Media indexada AB".middleEllipsize())
    }

    @Test
    fun `ellipsizes in the middle keeping brand and variant`() {
        assertEquals("Media i...a A CO 5", "Media indexada A CO 5".middleEllipsize())
    }

    @Test
    fun `keeps the tail that distinguishes sibling proposals`() {
        val co5 = "Media indexada A CO 5".middleEllipsize()
        val co3 = "Media indexada A CO 3".middleEllipsize()
        assertEquals(false, co5 == co3)
    }

    @Test
    fun `is stable for a much longer title`() {
        assertEquals(
            "Tarifa ...3 Puntas",
            "Tarifa Regulada Indexada 3 Puntas".middleEllipsize(),
        )
    }
}
