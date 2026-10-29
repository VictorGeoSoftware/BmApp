package com.briel.marnisos.brielapp.domain.models

/**
 * What the backend currently allows the signed-in broker to do with reports.
 * Derived only from `/me/access` capabilities; never from tier, admin status or claims.
 */
data class ReportAccessModel(
    val canRequestComprehensiveReport: Boolean = false,
)
