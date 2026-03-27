package com.briel.marnisos.brielapp.domain.models

data class CurrentUserConditionsModel(
    val powerTermPriceByPeriod: Map<String, String>,
    val energyPriceByPeriod: Map<String, String>,
    val extraServices: String,
)
