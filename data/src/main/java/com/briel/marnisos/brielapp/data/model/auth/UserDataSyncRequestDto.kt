package com.briel.marnisos.brielapp.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserDataSyncRequestDto(
    val userData: UserDataPayloadDto
)
