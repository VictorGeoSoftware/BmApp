package com.briel.marnisos.brielapp.data.model.prices

import kotlinx.serialization.Serializable

@Serializable
data class FetchConsumptionReportByCupsRequest(
    val cupsCode: String,
)
