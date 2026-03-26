package com.briel.marnisos.brielapp.domain.usecases

fun interface PersistLastCompletedJobIdUseCase {
    suspend operator fun invoke(jobId: String)
    companion object Factory
}
