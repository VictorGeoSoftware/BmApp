package com.briel.marnisos.brielapp.data.repository

import com.briel.marnisos.brielapp.data.mappers.PriceMapper.mapToDomain
import com.briel.marnisos.brielapp.data.network.PriceTableApi
import com.briel.marnisos.brielapp.data.utils.UserConsumptionUtils.mapUserConsumptionResponseToDomain
import com.briel.marnisos.brielapp.domain.models.PriceTablesInformationModel
import com.briel.marnisos.brielapp.domain.models.UserConsumptionModel

interface Repository {
    suspend fun getPriceTables(): Result<PriceTablesInformationModel>
    suspend fun getUserConsumptionData(): Result<UserConsumptionModel>
}

// Provide Repository using injected api
fun buildRepository(priceTableApi: PriceTableApi): Repository = RepositoryImpl(priceTableApi)

private class RepositoryImpl(
    private val priceTableApi: PriceTableApi
) : Repository {
    override suspend fun getPriceTables(): Result<PriceTablesInformationModel> {
        return priceTableApi.getPriceTableResults().map { response ->
            PriceTablesInformationModel(
                priceTables = response.results.map { it.extractedTables.mapToDomain() },
                iva = response.iva,
                impuestoElectrico = response.impuestoElectrico
            )
        }
    }

    override suspend fun getUserConsumptionData(): Result<UserConsumptionModel> {
        return priceTableApi.getUserConsumptionData().map { response ->
            response.mapUserConsumptionResponseToDomain()
        }
    }
}
