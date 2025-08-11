package com.briel.marnisos.brielapp.data.model.prices

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Prices1Response(
    @SerialName("success") val success: Boolean,
    @SerialName("data") val data: List<Prices1Item>,
    @SerialName("total") val total: Int
)

@Serializable
data class Prices1Item(
    @SerialName("fileName") val fileName: String,
    @SerialName("tarifa") val tarifa: String,
    @SerialName("potencia_contratada") val potenciaContratada: String? = null,
    @SerialName("p1") val p1: Double? = null,
    @SerialName("p2") val p2: Double? = null,
    @SerialName("p3") val p3: Double? = null,
    @SerialName("p4") val p4: Double? = null,
    @SerialName("p5") val p5: Double? = null,
    @SerialName("p6") val p6: Double? = null
)
