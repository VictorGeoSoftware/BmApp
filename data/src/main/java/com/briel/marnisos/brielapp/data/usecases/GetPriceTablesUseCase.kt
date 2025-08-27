package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.domain.usecases.GetPriceTablesUseCase

fun GetPriceTablesUseCase.Factory.create(
    repository: Repository
): GetPriceTablesUseCase = GetPriceTablesUseCase {
    repository.getPriceTables()
}
