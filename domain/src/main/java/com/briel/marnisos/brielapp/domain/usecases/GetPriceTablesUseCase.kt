package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.PriceTablesInformationModel

fun interface GetPriceTablesUseCase {
    suspend operator fun invoke(): Result<PriceTablesInformationModel>
    companion object Factory
}
