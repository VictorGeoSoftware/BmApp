package com.briel.marnisos.brielapp.data.repository

import com.briel.marnisos.brielapp.data.mappers.toDomain
import com.briel.marnisos.brielapp.data.mappers.toDto
import com.briel.marnisos.brielapp.data.network.AuthApi
import com.briel.marnisos.brielapp.data.utils.awaitTaskResult
import com.briel.marnisos.brielapp.domain.models.AuthUserModel
import com.briel.marnisos.brielapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun loginWithEmail(email: String, password: String): Result<AuthUserModel> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).awaitTaskResult()
            val user = authResult.user ?: return Result.failure(IllegalStateException("User is null after login"))
            Result.success(user.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithGoogle(googleIdToken: String): Result<AuthUserModel> {
        return try {
            val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).awaitTaskResult()
            val user = authResult.user
                ?: return Result.failure(IllegalStateException("User is null after Google sign-in"))
            Result.success(user.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getIdToken(forceRefresh: Boolean): Result<String> {
        return try {
            val user = firebaseAuth.currentUser ?: return Result.failure(IllegalStateException("No authenticated user"))
            val tokenResult = user.getIdToken(forceRefresh).awaitTaskResult()
            val token = tokenResult.token ?: return Result.failure(IllegalStateException("Firebase token is null"))
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncUserData(idToken: String, userData: AuthUserModel): Result<Unit> {
        return authApi.syncUserData(idToken = idToken, userData = userData.toDto())
    }

    override fun getCurrentUser(): AuthUserModel? = firebaseAuth.currentUser?.toDomain()

    override suspend fun logout() {
        val tokenResult = getIdToken(forceRefresh = false)
        val token = tokenResult.getOrNull()
        if (!token.isNullOrBlank()) {
            authApi.logout(idToken = token)
        }
        firebaseAuth.signOut()
    }
}
