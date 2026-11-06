package com.briel.marnisos.brielapp.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserDataPayloadDto(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoURL: String?,
    val providerIds: List<String>
)
