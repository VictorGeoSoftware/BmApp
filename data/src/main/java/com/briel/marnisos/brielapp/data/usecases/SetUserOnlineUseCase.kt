package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.domain.usecases.SetUserOnlineUseCase

fun SetUserOnlineUseCase.Factory.create(
    repository: Repository
): SetUserOnlineUseCase = SetUserOnlineUseCase { name, email ->
    repository.setUserOnline(name = name, email = email)
}
