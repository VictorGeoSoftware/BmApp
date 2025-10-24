package com.briel.marnisos.brielapp.data.di

import com.briel.marnisos.brielapp.data.network.KtorClientProvider
import com.briel.marnisos.brielapp.data.network.PriceTableApi
import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.data.repository.buildRepository
import com.briel.marnisos.brielapp.data.usecases.create
import com.briel.marnisos.brielapp.domain.usecases.GetPriceTablesUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetUserConsumptionUseCase
import com.briel.marnisos.brielapp.domain.usecases.UploadConsumptionReportUseCase
import org.koin.dsl.module

val dataModule = module {
    // Provide a singleton HttpClient from KtorClientProvider
    single { KtorClientProvider.client }

    // Provide API implementation
    single { PriceTableApi(get()) }

    // Provide repository
    single<Repository> { buildRepository(get()) }

    // Provide use case
    factory<GetPriceTablesUseCase> { GetPriceTablesUseCase.Factory.create(get()) }
    factory<GetUserConsumptionUseCase> { GetUserConsumptionUseCase.Factory.create(get()) }
    factory<UploadConsumptionReportUseCase> { UploadConsumptionReportUseCase.Factory.create(get()) }
}
