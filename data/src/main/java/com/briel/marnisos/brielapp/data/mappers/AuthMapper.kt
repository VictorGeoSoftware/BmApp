package com.briel.marnisos.brielapp.data.mappers

import com.briel.marnisos.brielapp.data.model.auth.UserDataPayloadDto
import com.briel.marnisos.brielapp.domain.models.AuthUserModel
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toDomain(): AuthUserModel = AuthUserModel(
    uid = uid,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl?.toString(),
    providerIds = providerData.mapNotNull { it.providerId }
)

fun AuthUserModel.toDto(): UserDataPayloadDto = UserDataPayloadDto(
    uid = uid,
    email = email,
    displayName = displayName,
    photoURL = photoUrl,
    providerIds = providerIds
)
