package com.briel.marnisos.brielapp.data.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlin.coroutines.cancellation.CancellationException

/**
 * GET /me/access. Returns the raw body on 200 and a failure otherwise. A 401/403 here
 * is deliberately not treated as a sign-out signal: it only hides Premium actions.
 */
class UserAccessApi(
    private val client: HttpClient = KtorClientProvider.client,
    private val baseUrl: String = KtorClientProvider.baseUrl,
) {
    suspend fun fetchAccess(idToken: String): Result<String> {
        return try {
            val response = client.get("$baseUrl/me/access") {
                headers.append(HttpHeaders.Authorization, "Bearer $idToken")
            }
            if (response.status == HttpStatusCode.OK) {
                Result.success(response.bodyAsText())
            } else {
                Result.failure(IllegalStateException("Access check failed: ${response.status.value}"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
