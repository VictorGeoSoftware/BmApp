package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.AuthUserModel

fun interface LoginWithEmailUseCase {
    suspend operator fun invoke(email: String, password: String): Result<AuthUserModel>
    companion object Factory
}
