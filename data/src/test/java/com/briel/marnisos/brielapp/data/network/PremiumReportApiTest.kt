package com.briel.marnisos.brielapp.data.network

import com.briel.marnisos.brielapp.data.model.prices.ComparatorReportColumn
import com.briel.marnisos.brielapp.data.model.prices.ComparatorReportPdfRequest
import com.briel.marnisos.brielapp.data.model.prices.PremiumReportPdfRequest
import com.briel.marnisos.brielapp.domain.models.PremiumReportException
import com.briel.marnisos.brielapp.domain.models.PremiumReportFailure
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PremiumReportApiTest {

    private val comparison = ComparatorReportPdfRequest(
        supplyHolder = "Ana",
        supplyAddress = "Calle 1",
        cups = "ES001",
        tariffName = "2.0TD",
        powerTermRows = emptyList(),
        energyConsumedRows = emptyList(),
        iva = "21.00 %",
        impuestoElectrico = "5.11 %",
        customerConditions = ComparatorReportColumn(
            title = "CONDICIONES ACTUALES",
            powerTermItems = emptyList(),
            annualPowerTermCost = "1",
            consumedEnergyItems = emptyList(),
            annualEnergyCost = "2",
            extraServices = "0.00",
            electricalTax = "3",
            iva = "4",
            totalAnnualPrice = "10",
        ),
        proposals = emptyList(),
    )
    private val request = PremiumReportPdfRequest(jobId = "job-1", comparison = comparison)

    /** Same plugins as KtorClientProvider (DefaultRequest Accept json + ContentNegotiation). */
    private fun api(handler: suspend io.ktor.client.engine.mock.MockRequestHandleScope.(HttpRequestData) -> io.ktor.client.request.HttpResponseData) =
        PremiumReportApi(
            HttpClient(MockEngine(handler)) {
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true; explicitNulls = false }) }
                install(DefaultRequest) { headers.append(HttpHeaders.Accept, ContentType.Application.Json.toString()) }
            },
            "http://test/api/v1",
        )

    private fun error(status: HttpStatusCode, body: String) = api {
        respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
    }

    private suspend fun failureOf(api: PremiumReportApi): PremiumReportFailure =
        (api.generate("tok", request).exceptionOrNull() as PremiumReportException).failure

    @Test
    fun `A4 posts jobId and the unchanged Basic body with the exact Accept header`() = runTest {
        val pdf = byteArrayOf(0x25, 0x50, 0x44, 0x46)
        var seen: HttpRequestData? = null
        val result = api { req ->
            seen = req
            respond(pdf, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/pdf"))
        }.generate("tok", request)

        assertArrayEquals(pdf, result.getOrThrow())
        val req = seen!!
        assertEquals(HttpMethod.Post, req.method)
        assertEquals("http://test/api/v1/reports/premium-pdf", req.url.toString())
        assertEquals("Bearer tok", req.headers[HttpHeaders.Authorization])
        assertEquals(listOf("application/pdf, application/json"), req.headers.getAll(HttpHeaders.Accept))
        val body = Json.parseToJsonElement(String((req.body as OutgoingContent.ByteArrayContent).bytes())).jsonObject
        assertEquals(setOf("jobId", "comparison"), body.keys)
        assertEquals("job-1", body["jobId"]!!.jsonPrimitive.content)
        val expectedComparison = Json.parseToJsonElement(Json.encodeToString(ComparatorReportPdfRequest.serializer(), comparison))
        assertEquals(expectedComparison, body["comparison"])
    }

    @Test
    fun `A6 empty 200 body is a generation failure`() = runTest {
        val api = api { respond(ByteArray(0), HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/pdf")) }
        assertEquals(PremiumReportFailure.GENERATION_FAILED, failureOf(api))
    }

    @Test
    fun `coded errors are mapped by status and code, never by message`() = runTest {
        fun coded(code: String) = """{"success":false,"code":"$code","message":"server text","requestId":"r"}"""
        assertEquals(PremiumReportFailure.PREMIUM_REQUIRED, failureOf(error(HttpStatusCode.Forbidden, coded("premium_required"))))
        assertEquals(PremiumReportFailure.ACCESS_REVOKED, failureOf(error(HttpStatusCode.Forbidden, """{"error":"Forbidden","message":"x"}""")))
        assertEquals(PremiumReportFailure.UNAUTHENTICATED, failureOf(error(HttpStatusCode.Unauthorized, """{"error":"Unauthorized"}""")))
        assertEquals(PremiumReportFailure.STUDY_NOT_FOUND, failureOf(error(HttpStatusCode.NotFound, coded("study_not_found"))))
        assertEquals(PremiumReportFailure.STUDY_NOT_READY, failureOf(error(HttpStatusCode.Conflict, coded("study_not_ready"))))
        assertEquals(PremiumReportFailure.HOLDER_MISSING, failureOf(error(HttpStatusCode.UnprocessableEntity, coded("holder_missing"))))
        assertEquals(PremiumReportFailure.ADDRESS_MISSING, failureOf(error(HttpStatusCode.UnprocessableEntity, coded("address_missing"))))
        assertEquals(PremiumReportFailure.CONDITIONS_MISSING, failureOf(error(HttpStatusCode.UnprocessableEntity, coded("current_conditions_missing"))))
        assertEquals(PremiumReportFailure.DATA_MISMATCH, failureOf(error(HttpStatusCode.UnprocessableEntity, coded("stale_offer"))))
        assertEquals(PremiumReportFailure.DATA_MISMATCH, failureOf(error(HttpStatusCode.BadRequest, coded("invalid_request"))))
        assertEquals(PremiumReportFailure.UNAVAILABLE, failureOf(error(HttpStatusCode.NotImplemented, coded("report_unavailable"))))
        assertEquals(PremiumReportFailure.GENERATION_FAILED, failureOf(error(HttpStatusCode.InternalServerError, coded("report_generation_failed"))))
        assertEquals(PremiumReportFailure.GENERATION_FAILED, failureOf(error(HttpStatusCode.NotAcceptable, "")))
        assertEquals(PremiumReportFailure.GENERATION_FAILED, failureOf(error(HttpStatusCode.UnprocessableEntity, "<html>nope</html>")))
        assertEquals(PremiumReportFailure.GENERATION_FAILED, failureOf(error(HttpStatusCode.BadGateway, coded("study_not_found"))))
    }

    @Test
    fun `A17 network exceptions are generation failures`() = runTest {
        assertEquals(PremiumReportFailure.GENERATION_FAILED, failureOf(api { throw java.io.IOException("offline") }))
    }

    @Test
    fun `cancellation is rethrown, never mapped`() = runTest {
        assertThrows<CancellationException> {
            api { throw CancellationException("cancelled") }.generate("tok", request)
        }
    }

    @Test
    fun `failure exceptions never carry server text`() = runTest {
        val e = error(HttpStatusCode.NotFound, """{"success":false,"code":"study_not_found","message":"secret server text"}""")
            .generate("tok", request).exceptionOrNull()!!
        assertFalse(e.message.orEmpty().contains("secret"))
        assertTrue(e is PremiumReportException)
    }
}
