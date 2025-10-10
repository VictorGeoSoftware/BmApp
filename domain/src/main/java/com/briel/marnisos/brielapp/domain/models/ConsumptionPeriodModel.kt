package com.briel.marnisos.brielapp.domain.models

data class ConsumptionPeriodModel(
    val fechaLecturaInicial: String,
    val fechaLecturaFinal: String,
    val activa: List<Double>,
    val reactiva: List<Double>,
    val maximetro: List<Double>
)
