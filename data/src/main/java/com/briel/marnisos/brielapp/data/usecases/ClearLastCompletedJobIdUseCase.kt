package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.domain.usecases.ClearLastCompletedJobIdUseCase

fun ClearLastCompletedJobIdUseCase.Factory.create(
    repository: Repository
): ClearLastCompletedJobIdUseCase = ClearLastCompletedJobIdUseCase {
    repository.clearLastCompletedJobId()
}
