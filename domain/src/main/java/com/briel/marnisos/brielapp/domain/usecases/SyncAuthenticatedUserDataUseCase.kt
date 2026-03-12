package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.AuthUserModel

fun interface SyncAuthenticatedUserDataUseCase {
    suspend operator fun invoke(idToken: String, userData: AuthUserModel): Result<Unit>
    companion object Factory
}
