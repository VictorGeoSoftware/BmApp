package com.briel.marnisos.brielapp.domain.models

data class PriceTablesInformationModel(
    val priceTables: List<PriceTablesModel>,
    val iva: Int,
    val impuestoElectrico: Double,
)
