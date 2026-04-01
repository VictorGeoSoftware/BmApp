package com.briel.marnisos.brielapp.domain.usecases

fun interface SetUserOfflineUseCase {
    suspend operator fun invoke(name: String, email: String): Result<Unit>
    companion object Factory
}
