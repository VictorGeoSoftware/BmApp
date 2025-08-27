package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.PriceTables

fun interface GetPriceTablesUseCase {
    suspend operator fun invoke(): Result<List<PriceTables>>
    companion object Factory
}
