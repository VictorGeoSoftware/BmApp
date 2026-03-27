package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.CurrentUserConditionsModel

fun interface PersistCurrentUserConditionsUseCase {
    suspend operator fun invoke(currentUserConditions: CurrentUserConditionsModel)
    companion object Factory
}
