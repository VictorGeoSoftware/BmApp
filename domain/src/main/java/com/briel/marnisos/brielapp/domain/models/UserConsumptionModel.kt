package com.briel.marnisos.brielapp.domain.models

data class UserConsumptionModel(
    val feeType: String,
    val data: List<ConsumptionPeriodModel>,
    val cups: String,
    val annualConsumption: Double,
    val annualConsumptionP1: Double,
    val annualConsumptionP2: Double,
    val annualConsumptionP3: Double,
    val annualConsumptionP4: Double,
    val annualConsumptionP5: Double,
    val annualConsumptionP6: Double,
    val subscribedPowerP1: Double,
    val subscribedPowerP2: Double,
    val subscribedPowerP6: Double,
    val subscribedPowerP3: Double,
    val subscribedPowerP4: Double,
    val subscribedPowerP5: Double,
)
