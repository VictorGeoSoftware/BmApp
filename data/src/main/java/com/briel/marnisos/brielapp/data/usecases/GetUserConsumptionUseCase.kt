package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.domain.usecases.GetUserConsumptionUseCase

fun GetUserConsumptionUseCase.Factory.create(
    repository: Repository
): GetUserConsumptionUseCase = GetUserConsumptionUseCase {
    repository.getUserConsumptionData()
}
