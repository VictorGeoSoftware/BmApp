package com.briel.marnisos.brielapp.domain.models

/**
 * The "current conditions" column of the comparator, derived from the customer's
 * entered prices and the consumption of the active study.
 */
data class CustomerConditionsColumnModel(
    val powerTermItems: List<Double> = emptyList(),
    val annualPowerTermCost: String = "",
    val consumedEnergyItems: List<Double> = emptyList(),
    val annualEnergyCost: String = "",
    val extraServices: String = "",
    val electricTax: String = "",
    val iva: String = "",
    val totalAnnualPrice: String = "",
)
