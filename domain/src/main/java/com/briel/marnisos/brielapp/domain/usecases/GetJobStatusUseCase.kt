package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.JobStatusModel

fun interface GetJobStatusUseCase {
    suspend operator fun invoke(jobId: String): Result<JobStatusModel>
    companion object Factory
}
