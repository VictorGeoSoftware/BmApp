package com.briel.marnisos.brielapp.data.model.prices

import kotlinx.serialization.Serializable

/** Only the machine-readable code of a report error; the server message is ignored. */
@Serializable
data class ReportErrorCodeResponse(
    val code: String? = null,
)
