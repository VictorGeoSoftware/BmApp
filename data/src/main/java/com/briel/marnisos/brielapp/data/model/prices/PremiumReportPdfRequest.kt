package com.briel.marnisos.brielapp.data.model.prices

import kotlinx.serialization.Serializable

/** Body of POST /reports/premium-pdf: the existing study plus the unchanged Basic body. */
@Serializable
data class PremiumReportPdfRequest(
    val jobId: String,
    val comparison: ComparatorReportPdfRequest,
)
