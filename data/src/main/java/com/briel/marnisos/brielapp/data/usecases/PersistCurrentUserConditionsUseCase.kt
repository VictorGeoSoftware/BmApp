package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.domain.usecases.PersistCurrentUserConditionsUseCase

fun PersistCurrentUserConditionsUseCase.Factory.create(
    repository: Repository,
): PersistCurrentUserConditionsUseCase = PersistCurrentUserConditionsUseCase { currentUserConditions ->
    repository.persistCurrentUserConditions(currentUserConditions)
}
