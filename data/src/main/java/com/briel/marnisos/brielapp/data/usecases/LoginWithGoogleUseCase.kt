package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.domain.repository.AuthRepository
import com.briel.marnisos.brielapp.domain.usecases.LoginWithGoogleUseCase

fun LoginWithGoogleUseCase.Factory.create(
    authRepository: AuthRepository
): LoginWithGoogleUseCase = LoginWithGoogleUseCase { googleIdToken ->
    authRepository.loginWithGoogle(googleIdToken)
}
