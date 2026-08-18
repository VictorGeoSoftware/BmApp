package com.briel.marnisos.brielapp.ui.navigation

/**
 * Stable analytics identifier for each route.
 *
 * Kept separate from the route declarations so navigation stays free of telemetry
 * concerns, and written out exhaustively rather than derived from the class name:
 * these names must survive refactors, since renaming one silently breaks every
 * historical report built on it. Naming matches the Crashlytics `screen_name` keys.
 */
val BmAppRoute.analyticsScreenName: String
    get() = when (this) {
        BmAppRoute.FetchConsumption -> "fetch_consumption"
        BmAppRoute.CupsScanner -> "cups_scanner"
        BmAppRoute.CurrentConditions -> "current_conditions"
        BmAppRoute.Proposals -> "proposals"
        BmAppRoute.Configuration -> "configuration"
    }

/** Login is rendered by `MainActivity`, outside the NavHost, so it has no route. */
const val LOGIN_SCREEN_NAME = "auth_login"
