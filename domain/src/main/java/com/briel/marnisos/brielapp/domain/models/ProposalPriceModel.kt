package com.briel.marnisos.brielapp.domain.models

import java.util.Locale

data class ProposalPriceModel(
    val proposalTitle: String,
    val powerTermItems: List<Double>,
    val annualPowerTermCost: Double,
    val consumedEnergyItems: List<Double>,
    val annualEnergyCost: Double,
    val extraServices: Double,
    val iva: Double,
    val electricalTax: Double,
    val totalAnnualPrice: Double
) {
    val annualPowerTermCostFormatted: String
        get() = annualPowerTermCost.toTwoDecimalsString()

    val annualEnergyCostFormatted: String
        get() = annualEnergyCost.toTwoDecimalsString()

    val extraServicesFormatted: String
        get() = extraServices.toTwoDecimalsString()

    val ivaFormatted: String
        get() = iva.toTwoDecimalsString()

    val electricalTaxFormatted: String
        get() = electricalTax.toTwoDecimalsString()

    val totalAnnualPriceFormatted: String
        get() = totalAnnualPrice.toTwoDecimalsString()
}

private fun Double.toTwoDecimalsString(): String = String.format(Locale.US, "%.2f", this)
