package com.briel.marnisos.brielapp.data.model.prices

import kotlinx.serialization.Serializable

@Serializable
data class ProposalPriceData(
    val proposalTitle: String,
    val powerTermItems: List<Double>,
    val annualPowerTermCost: Double,
    val consumedEnergyItems: List<Double>,
    val annualEnergyCost: Double,
    val extraServices: Double,
    val iva: Double,
    val electricalTax: Double,
    val totalAnnualPrice: Double
)
