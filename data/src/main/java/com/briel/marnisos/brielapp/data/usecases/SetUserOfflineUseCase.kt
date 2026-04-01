package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.domain.usecases.SetUserOfflineUseCase

fun SetUserOfflineUseCase.Factory.create(
    repository: Repository
): SetUserOfflineUseCase = SetUserOfflineUseCase { name, email ->
    repository.setUserOffline(name = name, email = email)
}
