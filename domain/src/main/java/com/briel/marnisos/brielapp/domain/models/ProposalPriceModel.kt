package com.briel.marnisos.brielapp.domain.models

data class ProposalPriceModel(
    val proposalTitle: String,
    val powerTermItems: List<String>,
    val annualPowerTermCost: String,
    val consumedEnergyItems: List<String>,
    val annualEnergyCost: String,
    val extraPricingItems: List<String>,
    val totalAnnualPrice: String,
    val savings: Pair<String, String>,
)
