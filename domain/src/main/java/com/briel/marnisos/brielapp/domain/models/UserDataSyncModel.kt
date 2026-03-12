package com.briel.marnisos.brielapp.domain.models

data class UserDataSyncModel(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val providerIds: List<String>
)
