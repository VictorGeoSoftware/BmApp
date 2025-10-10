package com.briel.marnisos.brielapp.data.utils

import com.briel.marnisos.brielapp.data.model.prices.ConsumptionPeriod
import com.briel.marnisos.brielapp.data.model.prices.UserConsumptionResponse
import com.briel.marnisos.brielapp.domain.models.ConsumptionPeriodModel
import com.briel.marnisos.brielapp.domain.models.UserConsumptionModel

object UserConsumptionUtils {
    fun UserConsumptionResponse.mapUserConsumptionResponseToDomain(): UserConsumptionModel {
        val data = this.data.map { it.mapToDomain() }
        
        return UserConsumptionModel(
            feeType = this.feeType,
            data = data,
            cups = this.cups,
            annualConsumption = this.annualConsumption,
            annualConsumptionP1 = this.annualConsumptionP1,
            annualConsumptionP2 = this.annualConsumptionP2,
            annualConsumptionP3 = this.annualConsumptionP3,
            annualConsumptionP4 = this.annualConsumptionP4,
            annualConsumptionP5 = this.annualConsumptionP5,
            annualConsumptionP6 = this.annualConsumptionP6,
            subscribedPowerP1 = this.subscribedPowerP1,
            subscribedPowerP2 = this.subscribedPowerP2,
            subscribedPowerP6 = this.subscribedPowerP6,
            subscribedPowerP3 = this.subscribedPowerP3,
            subscribedPowerP4 = this.subscribedPowerP4,
            subscribedPowerP5 = this.subscribedPowerP5,
        )
    }
    
    private fun ConsumptionPeriod.mapToDomain(): ConsumptionPeriodModel {
        return ConsumptionPeriodModel(
            fechaLecturaInicial = this.fechaLecturaInicial,
            fechaLecturaFinal = this.fechaLecturaFinal,
            activa = this.activa,
            reactiva = this.reactiva,
            maximetro = this.maximetro
        )
    }
}