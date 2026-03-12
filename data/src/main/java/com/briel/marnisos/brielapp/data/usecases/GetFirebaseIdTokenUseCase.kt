package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.domain.repository.AuthRepository
import com.briel.marnisos.brielapp.domain.usecases.GetFirebaseIdTokenUseCase

fun GetFirebaseIdTokenUseCase.Factory.create(
    authRepository: AuthRepository
): GetFirebaseIdTokenUseCase = GetFirebaseIdTokenUseCase { forceRefresh ->
    authRepository.getIdToken(forceRefresh)
}
