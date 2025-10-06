package com.briel.marnisos.brielapp.data.mappers

import com.briel.marnisos.brielapp.data.model.prices.ExtractedTables
import com.briel.marnisos.brielapp.data.model.prices.TablaPrecioClasicaBase
import com.briel.marnisos.brielapp.data.model.prices.TablaPrecioClasicaUnica
import com.briel.marnisos.brielapp.data.model.prices.TablaPrecioPotencia
import com.briel.marnisos.brielapp.data.model.prices.TarifaEnergia
import com.briel.marnisos.brielapp.data.model.prices.TarifaPotencia
import com.briel.marnisos.brielapp.data.model.prices.TerminoDeEnergia
import com.briel.marnisos.brielapp.domain.models.BaseClassicPrice
import com.briel.marnisos.brielapp.domain.models.EnergyTerm
import com.briel.marnisos.brielapp.domain.models.FeeModel
import com.briel.marnisos.brielapp.domain.models.PowerTerm
import com.briel.marnisos.brielapp.domain.models.PriceTablesModel
import com.briel.marnisos.brielapp.domain.models.UniqueClassicPrice

object PriceMapper {
    fun ExtractedTables.mapToDomain(): PriceTablesModel {
        return PriceTablesModel(
            companyName = this.companyName,
            powerTerm = this.terminoDePotencia.tablaPrecioPotencia.mapToDomain(),
            energyTerm = this.terminoDeEnergia.mapToDomain()
        )
    }

    private fun TablaPrecioPotencia.mapToDomain(): PowerTerm {
        return PowerTerm(
            name = this.titulo,
            powerFee = this.tarifas.map { it.mapToDomain() }
        )
    }

    private fun TarifaPotencia.mapToDomain(): FeeModel {
        return FeeModel(
            feeName = this.tarifa,
            contractedPower = this.potenciaContratada,
            p1 = this.p1,
            p2 = this.p2,
            p3 = this.p3,
            p4 = this.p4,
            p5 = this.p5,
            p6 = this.p6,
        )
    }

    private fun TerminoDeEnergia.mapToDomain(): EnergyTerm {
        return EnergyTerm(
            title = this.titulo,
            baseClassicPrice = this.tablaPrecioClasicaBase.mapToDomain(),
            uniqueClassicPrice = this.tablaPrecioClasicaUnica?.mapToDomain()
                ?: UniqueClassicPrice(title = "", fees = emptyList())
        )
    }

    private fun TablaPrecioClasicaBase.mapToDomain(): BaseClassicPrice {
        return BaseClassicPrice(
            title = this.titulo,
            fees = this.tarifas.map { it.mapToDomain() }
        )
    }

    private fun TablaPrecioClasicaUnica.mapToDomain(): UniqueClassicPrice {
        return UniqueClassicPrice(
            title = this.titulo,
            fees = this.tarifas.map { it.mapToDomain() }
        )
    }

    private fun TarifaEnergia.mapToDomain(): FeeModel {
        return FeeModel(
            feeName = this.tarifa,
            contractedPower = this.potenciaContratada,
            p1 = this.p1,
            p2 = this.p2,
            p3 = this.p3,
            p4 = this.p4,
            p5 = this.p5,
            p6 = this.p6,
        )
    }
}
