package com.briel.marnisos.brielapp.data.network

import com.briel.marnisos.brielapp.data.model.prices.Prices1Response
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.JsonElement

class PriceTableApi(
    val client: HttpClient = KtorClientProvider.client,
    val baseUrl: String = KtorClientProvider.baseUrl
) {
    // POST /store-price-tables
    suspend fun storePriceTables(payload: JsonElement): JsonElement {
        val resp = client.post("$baseUrl/store-price-tables") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }
        return resp.body()
    }

    // POST /batch-process-price-tables
    suspend fun batchProcessPriceTables(payload: JsonElement): JsonElement {
        val resp = client.post("$baseUrl/batch-process-price-tables") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }
        return resp.body()
    }

    // GET /price-tables
    suspend fun getPriceTables(
        limit: Int? = null,
        offset: Int? = null,
        filename: String? = null,
        source: String? = null
    ): JsonElement {
        val resp = client.get("$baseUrl/price-tables") {
            limit?.let { parameter("limit", it) }
            offset?.let { parameter("offset", it) }
            filename?.let { parameter("filename", it) }
            source?.let { parameter("source", it) }
        }
        return resp.body()
    }

    // GET /price-tables/{id}
    suspend fun getPriceTableById(id: Int): JsonElement {
        val resp = client.get("$baseUrl/price-tables/$id")
        return resp.body()
    }

    // GET /prices-1
    suspend fun getPrices1(
        limit: Int? = null,
        offset: Int? = null,
        filename: String? = null
    ): Prices1Response {
        return client.get("$baseUrl/prices-1") {
            limit?.let { parameter("limit", it) }
            offset?.let { parameter("offset", it) }
            filename?.let { parameter("filename", it) }
        }.body()
    }

    // GET /prices-2
    suspend fun getPrices2(
        limit: Int? = null,
        offset: Int? = null,
        filename: String? = null
    ): JsonElement {
        val resp = client.get("$baseUrl/prices-2") {
            limit?.let { parameter("limit", it) }
            offset?.let { parameter("offset", it) }
            filename?.let { parameter("filename", it) }
        }
        return resp.body()
    }

    // GET /prices-3
    suspend fun getPrices3(
        limit: Int? = null,
        offset: Int? = null,
        filename: String? = null
    ): JsonElement {
        val resp = client.get("$baseUrl/prices-3") {
            limit?.let { parameter("limit", it) }
            offset?.let { parameter("offset", it) }
            filename?.let { parameter("filename", it) }
        }
        return resp.body()
    }

    // Optional typed variants
    suspend inline fun <reified T> getPriceTablesTyped(
        limit: Int? = null,
        offset: Int? = null,
        filename: String? = null,
        source: String? = null
    ): T {
        return client.get("$baseUrl/price-tables") {
            limit?.let { parameter("limit", it) }
            offset?.let { parameter("offset", it) }
            filename?.let { parameter("filename", it) }
            source?.let { parameter("source", it) }
        }.body()
    }

    suspend inline fun <reified T> getPriceTableByIdTyped(id: Int): T =
        client.get("$baseUrl/price-tables/$id").body()

    suspend inline fun <reified T> storePriceTablesTyped(payload: JsonElement): T =
        client.post("$baseUrl/store-price-tables") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }.body()

    suspend inline fun <reified T> batchProcessPriceTablesTyped(payload: JsonElement): T =
        client.post("$baseUrl/batch-process-price-tables") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }.body()
}
