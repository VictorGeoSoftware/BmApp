package com.briel.marnisos.brielapp.data.model.prices

import kotlinx.serialization.Serializable

@Serializable
data class JobSubmissionResponse(
    val jobId: String,
    val status: String
)
