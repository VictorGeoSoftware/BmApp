package com.briel.marnisos.brielapp.data.network

import com.briel.marnisos.brielapp.data.model.prices.PremiumReportPdfRequest
import com.briel.marnisos.brielapp.data.model.prices.ReportErrorCodeResponse
import com.briel.marnisos.brielapp.data.utils.PremiumReportFailureMapper
import com.briel.marnisos.brielapp.domain.models.PremiumReportException
import com.briel.marnisos.brielapp.domain.models.PremiumReportFailure
import io.ktor.client.HttpClient
import io.ktor.client.plugins.api.SendingRequest
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException

/**
 * POST /reports/premium-pdf. Accept must list JSON too: with `application/pdf` alone the
 * backend answers coded errors with 406. Errors are mapped by status + code only.
 */
class PremiumReportApi(
    private val client: HttpClient = KtorClientProvider.client,
    private val baseUrl: String = KtorClientProvider.baseUrl,
) {
    /**
     * Same engine and plugins as [client], plus one exact Accept value set at send time:
     * DefaultRequest and ContentNegotiation would otherwise add a second application/json.
     */
    private val premiumClient: HttpClient by lazy { client.config { install(ExactAcceptPlugin) } }

    suspend fun generate(idToken: String, request: PremiumReportPdfRequest): Result<ByteArray> {
        return try {
            val response = premiumClient.post("$baseUrl/reports/premium-pdf") {
                headers.append(HttpHeaders.Authorization, "Bearer $idToken")
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status == HttpStatusCode.OK) {
                val bytes = response.bodyAsBytes()
                if (bytes.isEmpty()) failure(PremiumReportFailure.GENERATION_FAILED) else Result.success(bytes)
            } else {
                val code = decodeCode(runCatching { response.bodyAsText() }.getOrNull())
                failure(PremiumReportFailureMapper.fromResponse(response.status.value, code))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            failure(PremiumReportFailure.GENERATION_FAILED)
        }
    }

    private fun decodeCode(body: String?): String? {
        if (body.isNullOrBlank()) return null
        return try {
            json.decodeFromString(ReportErrorCodeResponse.serializer(), body).code
        } catch (e: Exception) {
            null
        }
    }

    private fun failure(failure: PremiumReportFailure): Result<ByteArray> =
        Result.failure(PremiumReportException(failure))

    private companion object {
        const val ACCEPT = "application/pdf, application/json"
        val ExactAcceptPlugin = createClientPlugin("PremiumReportExactAccept") {
            on(SendingRequest) { request, _ ->
                request.headers.remove(HttpHeaders.Accept)
                request.headers.append(HttpHeaders.Accept, ACCEPT)
            }
        }
        val json = Json { ignoreUnknownKeys = true; isLenient = true }
    }
}
