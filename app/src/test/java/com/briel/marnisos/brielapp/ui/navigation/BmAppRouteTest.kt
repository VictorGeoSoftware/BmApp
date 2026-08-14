package com.briel.marnisos.brielapp.ui.navigation

import com.briel.marnisos.brielapp.domain.models.FeeFirstStage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The fee-first gate has to move the broker *forward* as well as block them, so
 * reachability is an exact set of stages rather than a minimum. A regression here
 * strands the broker on a screen that no longer applies to the flow.
 */
class BmAppRouteTest {

    @Test
    fun `fetch consumption is unreachable once a study exists`() {
        assertTrue(FeeFirstStage.CONSUMPTION_REQUIRED.canReach(BmAppRoute.FetchConsumption))
        assertFalse(
            FeeFirstStage.CURRENT_CONDITIONS_REQUIRED.canReach(BmAppRoute.FetchConsumption),
        )
        assertFalse(FeeFirstStage.UNLOCKED.canReach(BmAppRoute.FetchConsumption))
    }

    @Test
    fun `scanner follows the fetch step`() {
        assertTrue(FeeFirstStage.CONSUMPTION_REQUIRED.canReach(BmAppRoute.CupsScanner))
        assertFalse(FeeFirstStage.UNLOCKED.canReach(BmAppRoute.CupsScanner))
    }

    @Test
    fun `current conditions stays reachable once unlocked`() {
        assertFalse(FeeFirstStage.CONSUMPTION_REQUIRED.canReach(BmAppRoute.CurrentConditions))
        assertTrue(
            FeeFirstStage.CURRENT_CONDITIONS_REQUIRED.canReach(BmAppRoute.CurrentConditions),
        )
        assertTrue(FeeFirstStage.UNLOCKED.canReach(BmAppRoute.CurrentConditions))
    }

    @Test
    fun `proposals and configuration need the full gate`() {
        listOf(BmAppRoute.Proposals, BmAppRoute.Configuration).forEach { route ->
            assertFalse(FeeFirstStage.CONSUMPTION_REQUIRED.canReach(route))
            assertFalse(FeeFirstStage.CURRENT_CONDITIONS_REQUIRED.canReach(route))
            assertTrue(FeeFirstStage.UNLOCKED.canReach(route))
        }
    }

    @Test
    fun `every stage has a reachable fallback`() {
        FeeFirstStage.entries.forEach { stage ->
            assertTrue(
                "fallback for $stage must be reachable from $stage",
                stage.canReach(stage.fallbackRoute()),
            )
        }
    }

    @Test
    fun `fallback sends an ungated user back to the fetch step`() {
        assertEquals(
            BmAppRoute.FetchConsumption,
            FeeFirstStage.CONSUMPTION_REQUIRED.fallbackRoute(),
        )
    }
}
