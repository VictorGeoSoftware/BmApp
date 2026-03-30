package com.briel.marnisos.brielapp.domain.models

data class ComparatorReportPdfModel(
    val title: String = "COMPARATIVO ANUAL DE PRECIOS FIJOS",
    val supplyHolder: String,
    val supplyAddress: String,
    val cups: String,
    val tariffName: String,
    val powerTermRows: List<ComparatorReportPeriodValueModel>,
    val energyConsumedRows: List<ComparatorReportPeriodIntValueModel>,
    val iva: String,
    val impuestoElectrico: String,
    val customerConditions: ComparatorReportColumnModel,
    val proposals: List<ComparatorReportProposalModel>
)

data class ComparatorReportPeriodValueModel(
    val period: String,
    val value: Double
)

data class ComparatorReportPeriodIntValueModel(
    val period: String,
    val value: Int
)

data class ComparatorReportColumnModel(
    val title: String,
    val powerTermItems: List<Double>,
    val annualPowerTermCost: String,
    val consumedEnergyItems: List<Double>,
    val annualEnergyCost: String,
    val extraServices: String,
    val electricalTax: String,
    val iva: String,
    val totalAnnualPrice: String
)

data class ComparatorReportProposalModel(
    val title: String,
    val powerTermItems: List<Double>,
    val annualPowerTermCost: String,
    val consumedEnergyItems: List<Double>,
    val annualEnergyCost: String,
    val extraServices: String,
    val electricalTax: String,
    val iva: String,
    val totalAnnualPrice: String,
    val annualPriceDifference: String?,
    val annualSavingsPercentage: Int?
)
