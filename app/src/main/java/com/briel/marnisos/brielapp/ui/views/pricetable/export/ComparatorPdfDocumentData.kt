package com.briel.marnisos.brielapp.ui.views.pricetable.export

data class ComparatorPdfDocumentData(
    val tariffName: String,
    val powerTermRows: List<Pair<String, Double>>,
    val energyConsumedRows: List<Pair<String, Int>>,
    val iva: String,
    val impuestoElectrico: String,
    val proposals: List<ComparatorPdfProposalData>
)

data class ComparatorPdfProposalData(
    val title: String,
    val powerTermItems: List<Double>,
    val annualPowerTermCost: String,
    val consumedEnergyItems: List<Double>,
    val annualEnergyCost: String,
    val fixedAmount: String,
    val iva: String,
    val electricalTax: String,
    val totalAnnualPrice: String
)
