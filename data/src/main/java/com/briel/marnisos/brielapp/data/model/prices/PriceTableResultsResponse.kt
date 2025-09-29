package com.briel.marnisos.brielapp.data.model.prices

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PriceTableResultsResponse(
    val success: Boolean,
    val results: List<PriceTableResult>,
    val iva: Int,
    val impuestoElectrico: Double,
)

@Serializable
data class PriceTableResult(
    @SerialName("fileName") val fileName: String,
    @SerialName("extracted_tables") val extractedTables: ExtractedTables
)

@Serializable
data class ExtractedTables(
    @SerialName("filename") val companyName: String,
    @SerialName("termino_de_potencia") val terminoDePotencia: TerminoDePotencia,
    @SerialName("termino_de_energia") val terminoDeEnergia: TerminoDeEnergia
)

@Serializable
data class TerminoDePotencia(
    @SerialName("titulo") val titulo: String,
    @SerialName("tabla_precio_potencia") val tablaPrecioPotencia: TablaPrecioPotencia
)

@Serializable
data class TablaPrecioPotencia(
    @SerialName("titulo") val titulo: String,
    @SerialName("tarifas") val tarifas: List<TarifaPotencia>
)

@Serializable
data class TarifaPotencia(
    @SerialName("tarifa") val tarifa: String,
    @SerialName("potencia_contratada") val potenciaContratada: String? = null,
    @SerialName("P1") val p1: Double? = null,
    @SerialName("P2") val p2: Double? = null,
    @SerialName("P3") val p3: Double? = null,
    @SerialName("P4") val p4: Double? = null,
    @SerialName("P5") val p5: Double? = null,
    @SerialName("P6") val p6: Double? = null
)

@Serializable
data class TerminoDeEnergia(
    @SerialName("titulo") val titulo: String,
    @SerialName("tabla_precio_clasica_base") val tablaPrecioClasicaBase: TablaPrecioClasicaBase,
    @SerialName("tabla_precio_clasica_unica") val tablaPrecioClasicaUnica: TablaPrecioClasicaUnica? = null
)

@Serializable
data class TablaPrecioClasicaBase(
    @SerialName("titulo") val titulo: String,
    @SerialName("tarifas") val tarifas: List<TarifaEnergia>
)

@Serializable
data class TablaPrecioClasicaUnica(
    @SerialName("titulo") val titulo: String,
    @SerialName("tarifas") val tarifas: List<TarifaEnergia>
)

@Serializable
data class TarifaEnergia(
    @SerialName("tarifa") val tarifa: String,
    @SerialName("potencia_contratada") val potenciaContratada: String? = null,
    @SerialName("P1") val p1: Double? = null,
    @SerialName("P2") val p2: Double? = null,
    @SerialName("P3") val p3: Double? = null,
    @SerialName("P4") val p4: Double? = null,
    @SerialName("P5") val p5: Double? = null,
    @SerialName("P6") val p6: Double? = null
)
