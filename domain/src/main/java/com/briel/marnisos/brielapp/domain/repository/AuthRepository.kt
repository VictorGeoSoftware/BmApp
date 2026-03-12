package com.briel.marnisos.brielapp.domain.repository

import com.briel.marnisos.brielapp.domain.models.AuthUserModel

interface AuthRepository {
    suspend fun loginWithEmail(email: String, password: String): Result<AuthUserModel>
    suspend fun getIdToken(forceRefresh: Boolean = false): Result<String>
    suspend fun syncUserData(idToken: String, userData: AuthUserModel): Result<Unit>
    fun getCurrentUser(): AuthUserModel?
    suspend fun logout()
}
