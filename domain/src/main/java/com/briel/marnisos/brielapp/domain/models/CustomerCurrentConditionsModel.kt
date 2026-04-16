package com.briel.marnisos.brielapp.domain.models

data class CustomerCurrentConditionsModel(
    val cups: String? = null,
    val atrTariff: String? = null,
    val contractedPowerPuntaKw: Double? = null,
    val contractedPowerValleKw: Double? = null,
    val powerTerms: List<PowerTermConditionModel> = emptyList(),
    val energyTerms: List<EnergyTermConditionModel> = emptyList(),
    val extraServices: List<ExtraServiceConditionModel> = emptyList(),
    val ivaPercentage: Double? = null,
    val electricalTaxPercentage: Double? = null,
    val billingPeriodDays: Int? = null,
    val totalAmount: Double? = null,
    val loyaltyDiscount: Double? = null,
)

data class PowerTermConditionModel(
    val period: String,
    val contractedKw: Double? = null,
    val pricePerKwDay: Double? = null,
    val billedAmount: Double? = null,
    val periodDescription: String? = null,
)

data class EnergyTermConditionModel(
    val pricePerKwh: Double? = null,
    val consumedKwh: Double? = null,
    val billedAmount: Double? = null,
    val periodDescription: String? = null,
)

data class ExtraServiceConditionModel(
    val name: String,
    val unitPrice: Double? = null,
    val unitType: String? = null,
    val quantity: Double? = null,
    val totalBilled: Double? = null,
    val isMeterRental: Boolean = false,
)
