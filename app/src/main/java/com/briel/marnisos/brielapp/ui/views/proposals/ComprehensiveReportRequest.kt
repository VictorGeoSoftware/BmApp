package com.briel.marnisos.brielapp.ui.views.proposals

import com.briel.marnisos.brielapp.domain.models.ComparatorReportPdfModel

/** A comprehensive-report request as sent; kept so Retry re-sends exactly the same one. */
internal data class ComprehensiveReportRequest(
    val jobId: String,
    val comparison: ComparatorReportPdfModel,
    val forceTokenRefresh: Boolean,
)
