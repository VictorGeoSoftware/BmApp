package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.AuthUserModel

fun interface GetCurrentAuthUserUseCase {
    operator fun invoke(): AuthUserModel?
    companion object Factory
}
