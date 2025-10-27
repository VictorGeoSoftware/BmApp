package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.domain.usecases.GetJobStatusUseCase

fun GetJobStatusUseCase.Factory.create(
    repository: Repository
): GetJobStatusUseCase = GetJobStatusUseCase { jobId ->
    repository.getJobStatus(jobId)
}
