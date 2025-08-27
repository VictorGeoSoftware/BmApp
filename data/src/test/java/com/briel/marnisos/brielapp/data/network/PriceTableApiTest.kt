package com.briel.marnisos.brielapp.data.network

import io.ktor.client.HttpClient
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PriceTableApiTest {

    private lateinit var mockHttpClient: HttpClient
    private lateinit var priceTableApi: PriceTableApi

    @BeforeEach
    fun setup() {
        mockHttpClient = KtorClientProvider.client
        priceTableApi = PriceTableApi(client = mockHttpClient)
    }

    @Test
    fun `api should return right response`() = runTest {
        // When
        val response = priceTableApi.getPriceTableResults()
        println("victor - response: $response")

        // Then
        assertEquals(true, response.isSuccess)
    }
}
