package com.briel.marnisos.brielapp.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UserAccessApiTest {

    @Test
    fun `200 returns the raw body and sends GET me-access with the bearer`() = runTest {
        val engine = MockEngine { request ->
            assertEquals(HttpMethod.Get, request.method)
            assertEquals("http://test/api/v1/me/access", request.url.toString())
            assertEquals("Bearer tok", request.headers[HttpHeaders.Authorization])
            respond("""{"tier":"BASIC","capabilities":[]}""", HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        val api = UserAccessApi(HttpClient(engine), "http://test/api/v1")

        val result = api.fetchAccess("tok")

        assertEquals("""{"tier":"BASIC","capabilities":[]}""", result.getOrThrow())
    }

    @Test
    fun `non-200 statuses are failures (T6, T7)`() = runTest {
        listOf(
            HttpStatusCode.Unauthorized,
            HttpStatusCode.Forbidden,
            HttpStatusCode.NotFound,
            HttpStatusCode.InternalServerError,
            HttpStatusCode.ServiceUnavailable,
            HttpStatusCode.NoContent,
        ).forEach { status ->
            val api = UserAccessApi(HttpClient(MockEngine { respondError(status) }), "http://test/api/v1")
            assertTrue(api.fetchAccess("tok").isFailure, status.toString())
        }
    }

    @Test
    fun `network errors are failures (T6)`() = runTest {
        val api = UserAccessApi(HttpClient(MockEngine { throw java.io.IOException("offline") }), "http://test/api/v1")
        assertTrue(api.fetchAccess("tok").isFailure)
    }
}
