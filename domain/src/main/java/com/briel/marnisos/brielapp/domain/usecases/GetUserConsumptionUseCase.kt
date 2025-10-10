package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.UserConsumptionModel

fun interface GetUserConsumptionUseCase {
    suspend operator fun invoke(): Result<UserConsumptionModel>
    companion object Factory
}
