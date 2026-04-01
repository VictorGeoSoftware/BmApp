package com.briel.marnisos.brielapp.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserActivityEventDto(
    val name: String,
    val email: String
)
