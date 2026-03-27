package com.briel.marnisos.brielapp.ui.views.comparator.customerconditions

data class CustomerConditionsUiState(
    val powerTermItems: List<Double> = emptyList(),
    val annualPowerTermCost: String = "",
    val consumedEnergyItems: List<Double> = emptyList(),
    val annualEnergyCost: String = "",
    val extraServices: String = "",
    val electricTax: String = "",
    val iva: String = "",
    val totalAnnualPrice: String = "",
)
