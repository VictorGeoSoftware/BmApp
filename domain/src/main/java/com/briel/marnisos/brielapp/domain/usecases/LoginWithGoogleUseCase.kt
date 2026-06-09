package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.AuthUserModel

fun interface LoginWithGoogleUseCase {
    suspend operator fun invoke(googleIdToken: String): Result<AuthUserModel>
    companion object Factory
}
