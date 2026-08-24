package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.domain.usecases.SubmitCollectedPricesUseCase

fun SubmitCollectedPricesUseCase.Factory.create(
    repository: Repository
): SubmitCollectedPricesUseCase = SubmitCollectedPricesUseCase { collectedPrices ->
    repository.submitCollectedPrices(collectedPrices)
}
