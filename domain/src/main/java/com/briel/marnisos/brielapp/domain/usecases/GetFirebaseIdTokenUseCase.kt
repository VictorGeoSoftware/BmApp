package com.briel.marnisos.brielapp.domain.usecases

fun interface GetFirebaseIdTokenUseCase {
    suspend operator fun invoke(forceRefresh: Boolean): Result<String>
    companion object Factory
}
