package com.briel.marnisos.brielapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponseDto(
    val message: String? = null
)
