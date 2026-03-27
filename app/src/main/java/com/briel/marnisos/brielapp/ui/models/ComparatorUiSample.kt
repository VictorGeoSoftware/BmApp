package com.briel.marnisos.brielapp.ui.models

import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel

data class ComparatorUiSample(val tariffName: String = "2.0TD", val powerRows: List<Pair<String, Double>> = listOf(
        "P1" to 5.50,
        "P2" to 5.50,
    ), val energyRows: List<Pair<String, Int>> = listOf(
        "P1" to 438,
        "P2" to 407,
        "P3" to 454,
    ), val extras: List<Pair<String, String>> = listOf(
        "IMPUESTO ELÉCTRICO" to "5.11%",
        "IVA" to "21%",
),
    val proposals: List<ProposalPriceModel> = listOf(
        ProposalPriceModel(
            proposalTitle = "Total - 1",
            powerTermItems = listOf(0.21, 0.22),
            annualPowerTermCost = 120.83,
            consumedEnergyItems = listOf(0.08, 0.09, 0.07),
            annualEnergyCost = 24.43,
            extraServices = 32.22,
            iva = 45.77,
            electricalTax = 11.22,
            totalAnnualPrice = 553.32
        )
    )
)

