package com.briel.marnisos.brielapp.data.utils

import com.briel.marnisos.brielapp.data.model.prices.CollectedPricesRequest
import com.briel.marnisos.brielapp.domain.models.CollectedPricesModel

object CollectedPricesMapper {

    fun CollectedPricesModel.toData(): CollectedPricesRequest = CollectedPricesRequest(
        companyName = companyName,
        tariffType = tariffName,
        powerPrices = powerTermPriceByPeriod,
        energyPrices = energyPriceByPeriod,
        extraServices = extraServices,
    )
}
