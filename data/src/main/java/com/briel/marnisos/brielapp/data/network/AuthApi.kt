package com.briel.marnisos.brielapp.data.network

import com.briel.marnisos.brielapp.data.model.auth.UserDataPayloadDto
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
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
                return Result.failure(
                    IllegalStateException(responseBody.ifBlank { "Failed to sync user data" })
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
