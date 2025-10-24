package com.briel.marnisos.brielapp.domain.models

data class ConsumptionReportModel(
    val success: Boolean,
    val doclingData: DoclingExtractedDataModel,
    val consumptionData: CleanedConsumptionDataModel,
    val filteredPrices: PriceTablesInformationModel
)

data class DoclingExtractedDataModel(
    val cupsCode: String,
    val customerDetails: CustomerDetailsModel,
    val customerId: CustomerIdModel
)

data class CustomerDetailsModel(
    val address: String,
    val name: String
)

data class CustomerIdModel(
    val contextText: String,
    val originalFormat: String,
    val type: String,
    val value: String
)

data class CleanedConsumptionDataModel(
    val cups: String,
    val tarifa: String,
    val tarifaValue: String,
    val annualConsumption: Double,
    val annualConsumptionP1: Double,
    val annualConsumptionP2: Double,
    val annualConsumptionP3: Double,
    val annualConsumptionP4: Double,
    val annualConsumptionP5: Double,
    val annualConsumptionP6: Double,
    val subscribedPowerP1: Double,
    val subscribedPowerP2: Double,
    val subscribedPowerP3: Double,
    val subscribedPowerP4: Double,
    val subscribedPowerP5: Double,
    val subscribedPowerP6: Double,
    val feeType: String,
    val fileName: String,
    val processedAt: String
)
