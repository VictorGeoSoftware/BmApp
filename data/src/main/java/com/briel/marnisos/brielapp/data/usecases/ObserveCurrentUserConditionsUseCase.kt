package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.domain.usecases.ObserveCurrentUserConditionsUseCase

fun ObserveCurrentUserConditionsUseCase.Factory.create(
    repository: Repository,
): ObserveCurrentUserConditionsUseCase = ObserveCurrentUserConditionsUseCase {
    repository.observeCurrentUserConditions()
}
