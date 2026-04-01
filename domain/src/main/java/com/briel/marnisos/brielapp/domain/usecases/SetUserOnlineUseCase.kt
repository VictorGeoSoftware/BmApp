package com.briel.marnisos.brielapp.domain.usecases

fun interface SetUserOnlineUseCase {
    suspend operator fun invoke(name: String, email: String): Result<Unit>
    companion object Factory
}
