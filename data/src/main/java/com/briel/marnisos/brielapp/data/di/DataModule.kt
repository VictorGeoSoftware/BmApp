package com.briel.marnisos.brielapp.data.di

import com.briel.marnisos.brielapp.data.network.AuthApi
import com.briel.marnisos.brielapp.data.network.KtorClientProvider
import com.briel.marnisos.brielapp.data.network.PriceTableApi
import com.briel.marnisos.brielapp.data.repository.AuthRepositoryImpl
import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.data.repository.buildRepository
import com.briel.marnisos.brielapp.data.usecases.create
import com.briel.marnisos.brielapp.domain.repository.AuthRepository
import com.briel.marnisos.brielapp.domain.usecases.GetCurrentAuthUserUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetFirebaseIdTokenUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetJobResultUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetJobStatusUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetPriceTablesUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetUserConsumptionUseCase
import com.briel.marnisos.brielapp.domain.usecases.LoginWithEmailUseCase
import com.briel.marnisos.brielapp.domain.usecases.LogoutUseCase
import com.briel.marnisos.brielapp.domain.usecases.RefreshConsumptionReportUseCase
import com.briel.marnisos.brielapp.domain.usecases.SubmitConsumptionReportJobUseCase
import com.briel.marnisos.brielapp.domain.usecases.SyncAuthenticatedUserDataUseCase
import com.google.firebase.auth.FirebaseAuth
import org.koin.dsl.module

val dataModule = module {
    // Provide a singleton HttpClient from KtorClientProvider
    single { KtorClientProvider.client }

    // Provide API implementation
    single { PriceTableApi(get()) }
    single { AuthApi(get()) }

    // Firebase
    single { FirebaseAuth.getInstance() }

    // Provide repository
    single<Repository> { buildRepository(get()) }
    single<AuthRepository> { AuthRepositoryImpl(firebaseAuth = get(), authApi = get()) }

    // Provide use cases
    factory<GetPriceTablesUseCase> { GetPriceTablesUseCase.Factory.create(get()) }
    factory<GetUserConsumptionUseCase> { GetUserConsumptionUseCase.Factory.create(get()) }
    
    // Async job processing use cases
    factory<SubmitConsumptionReportJobUseCase> { SubmitConsumptionReportJobUseCase.Factory.create(get()) }
    factory<GetJobStatusUseCase> { GetJobStatusUseCase.Factory.create(get()) }
    factory<GetJobResultUseCase> { GetJobResultUseCase.Factory.create(get()) }
    factory<RefreshConsumptionReportUseCase> { RefreshConsumptionReportUseCase.Factory.create(get()) }

    // Auth use cases
    factory<LoginWithEmailUseCase> { LoginWithEmailUseCase.Factory.create(get()) }
    factory<GetCurrentAuthUserUseCase> { GetCurrentAuthUserUseCase.Factory.create(get()) }
    factory<GetFirebaseIdTokenUseCase> { GetFirebaseIdTokenUseCase.Factory.create(get()) }
    factory<SyncAuthenticatedUserDataUseCase> { SyncAuthenticatedUserDataUseCase.Factory.create(get()) }
    factory<LogoutUseCase> { LogoutUseCase.Factory.create(get()) }
}
