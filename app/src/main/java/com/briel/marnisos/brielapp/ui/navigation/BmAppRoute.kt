package com.briel.marnisos.brielapp.ui.navigation

import com.briel.marnisos.brielapp.domain.models.FeeFirstStage
import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes for the authenticated area of the app.
 *
 * [allowedStages] is the exact set of fee-first stages in which a route may be
 * displayed. It is a set rather than a minimum on purpose: the fetch-consumption
 * step is only valid *before* a study exists, so once one is loaded the NavHost
 * must move the broker forward instead of leaving them on a dead screen.
 */
sealed interface BmAppRoute {

    val allowedStages: Set<FeeFirstStage>

    /** Mandatory first step: fetch the consumption study by CUPS or PDF. */
    @Serializable
    data object FetchConsumption : BmAppRoute {
        override val allowedStages: Set<FeeFirstStage>
            get() = setOf(FeeFirstStage.CONSUMPTION_REQUIRED)
    }

    /** Camera scanner for the CUPS code. */
    @Serializable
    data object CupsScanner : BmAppRoute {
        override val allowedStages: Set<FeeFirstStage>
            get() = setOf(FeeFirstStage.CONSUMPTION_REQUIRED)
    }

    /** The gate itself: the customer's current prices. */
    @Serializable
    data object CurrentConditions : BmAppRoute {
        override val allowedStages: Set<FeeFirstStage>
            get() = setOf(FeeFirstStage.CURRENT_CONDITIONS_REQUIRED, FeeFirstStage.UNLOCKED)
    }

    @Serializable
    data object Proposals : BmAppRoute {
        override val allowedStages: Set<FeeFirstStage> get() = setOf(FeeFirstStage.UNLOCKED)
    }

    @Serializable
    data object Configuration : BmAppRoute {
        override val allowedStages: Set<FeeFirstStage> get() = setOf(FeeFirstStage.UNLOCKED)
    }
}

/** Whether [route] may be displayed while the flow is at [this] stage. */
fun FeeFirstStage.canReach(route: BmAppRoute): Boolean = this in route.allowedStages

/** The route the user must be sent to when the current stage blocks their destination. */
fun FeeFirstStage.fallbackRoute(): BmAppRoute = when (this) {
    FeeFirstStage.CONSUMPTION_REQUIRED -> BmAppRoute.FetchConsumption
    FeeFirstStage.CURRENT_CONDITIONS_REQUIRED -> BmAppRoute.CurrentConditions
    FeeFirstStage.UNLOCKED -> BmAppRoute.CurrentConditions
}
