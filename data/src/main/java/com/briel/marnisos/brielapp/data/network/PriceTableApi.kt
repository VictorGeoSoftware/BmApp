package com.briel.marnisos.brielapp.data.network

import com.briel.marnisos.brielapp.data.model.prices.ConsumptionReportResponse
import com.briel.marnisos.brielapp.data.model.prices.PriceTableResultsResponse
import com.briel.marnisos.brielapp.data.model.prices.UserConsumptionResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import java.io.File

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

    /**
     * Uploads a PDF file and fetches the consumption report with filtered price tables
     * POST /fetch-user-consumption-report
     * @param pdfFile The PDF file to upload
     */
    suspend fun uploadConsumptionReport(pdfFile: File): Result<ConsumptionReportResponse> {
        return try {
            val response = client.submitFormWithBinaryData(
                url = "$baseUrl/fetch-user-consumption-report",
                formData = formData {
                    append("file", pdfFile.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "application/pdf")
                        append(HttpHeaders.ContentDisposition, "filename=\"${pdfFile.name}\"")
                    })
                }
            )

            val status = response.status
            if (status == HttpStatusCode.OK) {
                val responseBody = response.body<ConsumptionReportResponse>()
                Result.success(responseBody)
            } else {
                Result.failure(Exception("Error uploading PDF: $status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
