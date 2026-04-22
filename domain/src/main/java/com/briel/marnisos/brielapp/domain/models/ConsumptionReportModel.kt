package com.briel.marnisos.brielapp.domain.models

data class ConsumptionReportModel(
    val success: Boolean,
    val userData: DoclingExtractedDataModel,
    val consumptionData: CleanedConsumptionDataModel,
    val proposals: List<ProposalPriceModel>,
    val iva: Double,
    val impuestoElectrico: Double
)

data class DoclingExtractedDataModel(
    val cupsCode: String,
    val customerDetails: CustomerDetailsModel? = null,
    val customerId: CustomerIdModel? = null
)

data class CustomerDetailsModel(
    val address: String? = null,
    val name: String? = null,
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
) {
    fun annualConsumptionValues(): List<Pair<String, Double>> {
        return listOf(
            annualConsumptionP1,
            annualConsumptionP2,
            annualConsumptionP3,
            annualConsumptionP4,
            annualConsumptionP5,
            annualConsumptionP6
        ).mapIndexed { index, p ->
            val number = "p${index + 1}".uppercase()
            Pair(number, p)
        }.filter { pair ->
            pair.second != ZERO
        }
    }

    fun subscribedPowerValues(): List<Pair<String, Double>> {
        return listOf(
            subscribedPowerP1,
            subscribedPowerP2,
            subscribedPowerP3,
            subscribedPowerP4,
            subscribedPowerP5,
            subscribedPowerP6
        ).mapIndexed { index, p ->
            val number = "p${index + 1}".uppercase()
            Pair(number, p)
        }.filter { pair ->
            pair.second != ZERO
        }
    }
}
