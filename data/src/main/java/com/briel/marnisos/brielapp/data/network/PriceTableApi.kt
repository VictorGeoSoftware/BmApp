package com.briel.marnisos.brielapp.data.network

import com.briel.marnisos.brielapp.data.model.prices.PriceTableResultsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class PriceTableApi(
    private val client: HttpClient = KtorClientProvider.client,
    private val baseUrl: String = "http://localhost:8081/api/v1"
) {
    /**
     * Fetches price table results from the new endpoint
     * GET /price-table-results
     */
    suspend fun getPriceTableResults(): PriceTableResultsResponse {
        return client.get("$baseUrl/price-table-results").body()
    }
}
