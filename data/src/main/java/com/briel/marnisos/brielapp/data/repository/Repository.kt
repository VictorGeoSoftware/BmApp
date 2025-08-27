package com.briel.marnisos.brielapp.data.repository

import com.briel.marnisos.brielapp.data.mappers.PriceMapper.mapToDomain
import com.briel.marnisos.brielapp.data.network.PriceTableApi
import com.briel.marnisos.brielapp.domain.models.PriceTables

interface Repository {
    suspend fun getPriceTables(): Result<List<PriceTables>>
}

// Provide Repository using injected api
fun buildRepository(priceTableApi: PriceTableApi): Repository = RepositoryImpl(priceTableApi)

private class RepositoryImpl(
    private val priceTableApi: PriceTableApi
) : Repository {
    override suspend fun getPriceTables(): Result<List<PriceTables>> {
        return priceTableApi.getPriceTableResults().map { response ->
            response.results
        }.map { priceTableResults ->
            priceTableResults.map { priceTableResult ->
                priceTableResult.extractedTables
            }.map { extractedTable ->
                extractedTable.mapToDomain()
            }
        }
    }
}
