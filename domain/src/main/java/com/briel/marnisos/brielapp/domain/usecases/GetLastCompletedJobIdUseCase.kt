package com.briel.marnisos.brielapp.domain.usecases

fun interface GetLastCompletedJobIdUseCase {
    suspend operator fun invoke(): String?
    companion object Factory
}
