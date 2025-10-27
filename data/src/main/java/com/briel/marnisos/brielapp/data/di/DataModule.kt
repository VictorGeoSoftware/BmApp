package com.briel.marnisos.brielapp.data.di

import com.briel.marnisos.brielapp.data.network.KtorClientProvider
import com.briel.marnisos.brielapp.data.network.PriceTableApi
import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.data.repository.buildRepository
import com.briel.marnisos.brielapp.data.usecases.create
import com.briel.marnisos.brielapp.domain.usecases.GetJobResultUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetJobStatusUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetPriceTablesUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetUserConsumptionUseCase
import com.briel.marnisos.brielapp.domain.usecases.SubmitConsumptionReportJobUseCase
import org.koin.dsl.module

val dataModule = module {
    // Provide a singleton HttpClient from KtorClientProvider
    single { KtorClientProvider.client }

    // Provide API implementation
    single { PriceTableApi(get()) }

    // Provide repository
    single<Repository> { buildRepository(get()) }

    // Provide use cases
    factory<GetPriceTablesUseCase> { GetPriceTablesUseCase.Factory.create(get()) }
    factory<GetUserConsumptionUseCase> { GetUserConsumptionUseCase.Factory.create(get()) }
    
    // Async job processing use cases
    factory<SubmitConsumptionReportJobUseCase> { SubmitConsumptionReportJobUseCase.Factory.create(get()) }
    factory<GetJobStatusUseCase> { GetJobStatusUseCase.Factory.create(get()) }
    factory<GetJobResultUseCase> { GetJobResultUseCase.Factory.create(get()) }
}
