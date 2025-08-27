package com.briel.marnisos.brielapp.data.network

import com.briel.marnisos.brielapp.data.model.prices.PriceTableResultsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

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
}
