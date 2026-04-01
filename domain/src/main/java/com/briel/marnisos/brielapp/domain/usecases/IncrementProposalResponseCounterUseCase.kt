package com.briel.marnisos.brielapp.domain.usecases

fun interface IncrementProposalResponseCounterUseCase {
    suspend operator fun invoke(name: String, email: String): Result<Unit>
    companion object Factory
}
