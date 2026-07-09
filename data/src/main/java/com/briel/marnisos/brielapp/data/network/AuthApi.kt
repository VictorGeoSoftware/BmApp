package com.briel.marnisos.brielapp.data.network

import com.briel.marnisos.brielapp.data.model.auth.UserDataPayloadDto
import com.briel.marnisos.brielapp.domain.error.AccessDeniedException
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

class AuthApi(
    private val client: HttpClient = KtorClientProvider.client,
    private val baseUrl: String = KtorClientProvider.baseUrl
) {
    suspend fun syncUserData(idToken: String, userData: UserDataPayloadDto): Result<Unit> {
        return try {
            val response = client.post("$baseUrl/user-data") {
                header(HttpHeaders.ContentType, "application/json")
                header(HttpHeaders.Authorization, "Bearer $idToken")
                setBody(userData)
            }
            if (!response.status.isSuccess()) {
                val responseBody = response.bodyAsText()
                if (response.status == HttpStatusCode.Forbidden) {
                    return Result.failure(AccessDeniedException(responseBody.ifBlank { null }))
                }
                return Result.failure(
                    IllegalStateException(responseBody.ifBlank { "Failed to sync user data" })
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(idToken: String): Result<Unit> {
        return try {
            val response = client.post("$baseUrl/auth/logout") {
                header(HttpHeaders.Authorization, "Bearer $idToken")
            }

            if (!response.status.isSuccess()) {
                val responseBody = response.bodyAsText()
                return Result.failure(
                    IllegalStateException(responseBody.ifBlank { "Failed to logout user" })
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
