package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.domain.usecases.ClearCurrentUserConditionsUseCase

fun ClearCurrentUserConditionsUseCase.Factory.create(
    repository: Repository,
): ClearCurrentUserConditionsUseCase = ClearCurrentUserConditionsUseCase {
    repository.clearCurrentUserConditions()
}
