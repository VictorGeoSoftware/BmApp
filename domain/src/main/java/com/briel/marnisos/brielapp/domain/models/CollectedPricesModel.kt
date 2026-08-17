package com.briel.marnisos.brielapp.domain.models

/**
 * The customer's current electricity prices, ready to be sent to the backend.
 *
 * Carries no customer personal data by design: no CUPS, no holder, no address. Only
 * the supplier, the tariff and the prices.
 *
 * Prices are keyed by period ("P1".."P6") and only contain the periods that apply to
 * the customer's tariff.
 */
data class CollectedPricesModel(
    val companyName: String,
    val tariffName: String,
    val powerTermPriceByPeriod: Map<String, Double>,
    val energyPriceByPeriod: Map<String, Double>,
    val extraServices: Double?,
)
