package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.domain.usecases.PersistLastCompletedJobIdUseCase

fun PersistLastCompletedJobIdUseCase.Factory.create(
    repository: Repository
): PersistLastCompletedJobIdUseCase = PersistLastCompletedJobIdUseCase { jobId ->
    repository.persistLastCompletedJobId(jobId)
}
