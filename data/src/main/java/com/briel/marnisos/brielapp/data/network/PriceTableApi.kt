package com.briel.marnisos.brielapp.data.network

import com.briel.marnisos.brielapp.data.model.prices.PriceTableResultsResponse
import com.briel.marnisos.brielapp.data.model.prices.UserConsumptionResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.util.Platform
import kotlinx.serialization.json.Json

class PriceTableApi(
    private val client: HttpClient = KtorClientProvider.client,
    private val baseUrl: String = KtorClientProvider.baseUrl
) {
    /**
     * Fetches price table results from the new endpoint
     * GET /price-table-results
     */
    suspend fun getPriceTableResults(): Result<PriceTableResultsResponse> {
        return try {
            val response = client.get("$baseUrl/price-table-results")
                .body<PriceTableResultsResponse>()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserConsumptionData(): Result<UserConsumptionResponse> {
        return try {
            val query = client.get("$baseUrl/fetch-user-consumption-report")
            val status = query.status

            if (status == HttpStatusCode.OK) {
                val response = query.body<UserConsumptionResponse>()
                Result.success(response)
            } else {
                Result.failure(Exception("Error: $status"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
