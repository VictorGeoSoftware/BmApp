package com.briel.marnisos.brielapp.domain.models

data class JobStatusModel(
    val jobId: String,
    val status: JobStatusType
)

enum class JobStatusType {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}
