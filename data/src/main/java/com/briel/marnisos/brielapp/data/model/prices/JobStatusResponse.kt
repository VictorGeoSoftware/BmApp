package com.briel.marnisos.brielapp.data.model.prices

import kotlinx.serialization.Serializable

@Serializable
data class JobStatusResponse(
    val jobId: String,
    val status: JobStatus
)

@Serializable
enum class JobStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}
