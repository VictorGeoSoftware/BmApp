package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.domain.usecases.GetJobResultUseCase

fun GetJobResultUseCase.Factory.create(
    repository: Repository
): GetJobResultUseCase = GetJobResultUseCase { jobId ->
    repository.getJobResult(jobId)
}
