package com.briel.marnisos.brielapp.domain.usecases

fun interface ClearCurrentUserConditionsUseCase {
    suspend operator fun invoke()
    companion object Factory
}
