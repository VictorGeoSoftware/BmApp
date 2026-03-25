package com.briel.marnisos.brielapp.data.network

import com.briel.marnisos.brielapp.data.model.prices.ConsumptionReportResponse
import com.briel.marnisos.brielapp.data.model.prices.JobStatusResponse
import com.briel.marnisos.brielapp.data.model.prices.JobSubmissionResponse
import com.briel.marnisos.brielapp.data.model.prices.PriceTableResultsResponse
import com.briel.marnisos.brielapp.data.model.prices.UserConsumptionResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
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
     * Submits a PDF file for async processing
     * POST /fetch-user-consumption-report
     * @param pdfFile The PDF file to upload
     * @return JobSubmissionResponse with jobId and initial status
     */
    suspend fun submitConsumptionReportJob(pdfFile: File): Result<JobSubmissionResponse> {
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
            if (status == HttpStatusCode.Accepted) {
                val responseBody = response.body<JobSubmissionResponse>()
                Result.success(responseBody)
            } else {
                Result.failure(Exception("Error submitting PDF job: $status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Checks the status of a consumption report job
     * GET /consumption-report-status/{jobId}
     * @param jobId The job ID to check
     * @return JobStatusResponse with current job status
     */
    suspend fun getJobStatus(jobId: String): Result<JobStatusResponse> {
        return try {
            val response = client.get("$baseUrl/consumption-report-status/$jobId")
            val status = response.status

            if (status == HttpStatusCode.OK) {
                val responseBody = response.body<JobStatusResponse>()
                Result.success(responseBody)
            } else {
                Result.failure(Exception("Error fetching job status: $status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Retrieves the result of a completed consumption report job
     * GET /consumption-report-result/{jobId}
     * @param jobId The job ID to retrieve results for
     * @return ConsumptionReportResponse with the processed data
     */
    suspend fun getJobResult(jobId: String): Result<ConsumptionReportResponse> {
        return try {
            val response = client.get("$baseUrl/consumption-report-result/$jobId")
            val status = response.status

            if (status == HttpStatusCode.OK) {
                val rawJson = response.bodyAsText()
                val json = Json { ignoreUnknownKeys = true }
                val responseBody = json.decodeFromString<ConsumptionReportResponse>(rawJson)
                Result.success(responseBody)
            } else {
                Result.failure(Exception("Error fetching job result: $status"))
            }
        } catch (e: Exception) {
            println("DEBUG - Exception parsing JSON: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun refreshConsumptionReport(jobId: String): Result<ConsumptionReportResponse> {
        return try {
            val response = client.post("$baseUrl/consumption-report-refresh/$jobId")
            val status = response.status

            if (status == HttpStatusCode.OK) {
                val rawJson = response.bodyAsText()
                val json = Json { ignoreUnknownKeys = true }
                val responseBody = json.decodeFromString<ConsumptionReportResponse>(rawJson)
                Result.success(responseBody)
            } else {
                Result.failure(Exception("Error refreshing job result: $status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
