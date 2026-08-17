package com.briel.marnisos.brielapp.domain.models

/**
 * The customer's current contract as entered by the broker.
 *
 * [companyName] is the customer's current supplier (comercializadora). It is typed by
 * the broker: neither the bill read nor the price proposals carry the incumbent
 * supplier, so there is nothing to prefill it from.
 */
data class CurrentUserConditionsModel(
    val companyName: String,
    val powerTermPriceByPeriod: Map<String, String>,
    val energyPriceByPeriod: Map<String, String>,
    val extraServices: String,
)
