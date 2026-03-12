package com.briel.marnisos.brielapp.domain.usecases

fun interface LogoutUseCase {
    suspend operator fun invoke()
    companion object Factory
}
