package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.domain.repository.AuthRepository
import com.briel.marnisos.brielapp.domain.usecases.LoginWithEmailUseCase

fun LoginWithEmailUseCase.Factory.create(
    authRepository: AuthRepository
): LoginWithEmailUseCase = LoginWithEmailUseCase { email, password ->
    authRepository.loginWithEmail(email, password)
}
