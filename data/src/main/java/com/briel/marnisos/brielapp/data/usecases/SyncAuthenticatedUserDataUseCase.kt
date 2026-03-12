package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.domain.repository.AuthRepository
import com.briel.marnisos.brielapp.domain.usecases.SyncAuthenticatedUserDataUseCase

fun SyncAuthenticatedUserDataUseCase.Factory.create(
    authRepository: AuthRepository
): SyncAuthenticatedUserDataUseCase = SyncAuthenticatedUserDataUseCase { idToken, userData ->
    authRepository.syncUserData(idToken, userData)
}
