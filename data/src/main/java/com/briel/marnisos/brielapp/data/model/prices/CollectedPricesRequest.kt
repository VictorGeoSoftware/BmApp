package com.briel.marnisos.brielapp.data.model.prices

import kotlinx.serialization.Serializable

/**
 * Body of `POST /api/v1/collected-prices`.
 *
 * Period maps are keyed "P1".."P6" and only carry the periods that apply to the
 * customer's tariff.
 */
@Serializable
data class CollectedPricesRequest(
    val companyName: String,
    val tariffType: String,
    val powerPrices: Map<String, Double>,
    val energyPrices: Map<String, Double>,
    val extraServices: Double? = null,
)
