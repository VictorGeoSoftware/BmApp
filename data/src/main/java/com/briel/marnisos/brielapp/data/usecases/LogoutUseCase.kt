package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.domain.repository.AuthRepository
import com.briel.marnisos.brielapp.domain.usecases.LogoutUseCase

fun LogoutUseCase.Factory.create(
    authRepository: AuthRepository
): LogoutUseCase = LogoutUseCase {
    authRepository.logout()
}
