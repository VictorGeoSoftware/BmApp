package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.domain.usecases.GetLastCompletedJobIdUseCase

fun GetLastCompletedJobIdUseCase.Factory.create(
    repository: Repository
): GetLastCompletedJobIdUseCase = GetLastCompletedJobIdUseCase {
    repository.getLastCompletedJobId()
}
