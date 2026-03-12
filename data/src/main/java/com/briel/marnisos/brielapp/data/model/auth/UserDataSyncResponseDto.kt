package com.briel.marnisos.brielapp.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserDataSyncResponseDto(
    val success: Boolean,
    val message: String? = null
)
