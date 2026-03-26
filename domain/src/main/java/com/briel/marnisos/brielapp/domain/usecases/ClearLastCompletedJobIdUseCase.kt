package com.briel.marnisos.brielapp.domain.usecases

fun interface ClearLastCompletedJobIdUseCase {
    suspend operator fun invoke()
    companion object Factory
}
