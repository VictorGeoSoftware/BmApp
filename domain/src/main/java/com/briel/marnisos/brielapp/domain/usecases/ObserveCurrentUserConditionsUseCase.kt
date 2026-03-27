package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.CurrentUserConditionsModel
import kotlinx.coroutines.flow.Flow

fun interface ObserveCurrentUserConditionsUseCase {
    operator fun invoke(): Flow<CurrentUserConditionsModel?>
    companion object Factory
}
