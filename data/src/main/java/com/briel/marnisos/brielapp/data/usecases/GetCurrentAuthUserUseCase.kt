package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.domain.repository.AuthRepository
import com.briel.marnisos.brielapp.domain.usecases.GetCurrentAuthUserUseCase

fun GetCurrentAuthUserUseCase.Factory.create(
    authRepository: AuthRepository
): GetCurrentAuthUserUseCase = GetCurrentAuthUserUseCase {
    authRepository.getCurrentUser()
}
