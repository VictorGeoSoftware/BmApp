package com.briel.marnisos.brielapp.data.model.prices

import kotlinx.serialization.Serializable

@Serializable
data class ConsumptionReportResponse(
    val success: Boolean,
    val userData: DoclingExtractedData,
    val currentConditions: CustomerCurrentConditionsResponse? = null,
    val consumptionData: CleanedConsumptionData,
    val proposals: List<ProposalPriceData>,
    val iva: Double,
    val impuestoElectrico: Double
)

@Serializable
data class DoclingExtractedData(
    val cups_code: String = "",
    val customer_details: CustomerDetails? = null,
    val customer_id: CustomerId? = null
)

@Serializable
data class CustomerDetails(
    val address: String? = null,
    val name: String? = null
)

@Serializable
data class CustomerId(
    val context_text: String,
    val original_format: String,
    val type: String,
    val value: String
)

@Serializable
data class CleanedConsumptionData(
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
