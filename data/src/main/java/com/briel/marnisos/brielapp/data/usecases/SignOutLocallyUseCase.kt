package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.domain.repository.AuthRepository
import com.briel.marnisos.brielapp.domain.usecases.SignOutLocallyUseCase

fun SignOutLocallyUseCase.Factory.create(
    authRepository: AuthRepository
): SignOutLocallyUseCase = SignOutLocallyUseCase {
    authRepository.signOutLocally()
}
