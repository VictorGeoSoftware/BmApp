package com.briel.marnisos.brielapp.data.di

import android.content.Context
import com.briel.marnisos.brielapp.data.local.CurrentUserConditionsLocalDataSource
import com.briel.marnisos.brielapp.data.local.DeviceIdLocalDataSource
import com.briel.marnisos.brielapp.data.local.LastCompletedJobIdLocalDataSource
import com.briel.marnisos.brielapp.data.network.AuthApi
import com.briel.marnisos.brielapp.data.network.KtorClientProvider
import com.briel.marnisos.brielapp.data.network.PriceTableApi
import com.briel.marnisos.brielapp.data.repository.AuthRepositoryImpl
import com.briel.marnisos.brielapp.data.repository.ConsumptionSessionRepositoryImpl
import com.briel.marnisos.brielapp.data.repository.ConsumptionStudyRepositoryImpl
import com.briel.marnisos.brielapp.data.repository.PriceUpdatesNotifierImpl
import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.data.repository.buildRepository
import com.briel.marnisos.brielapp.data.usecases.create
import com.briel.marnisos.brielapp.domain.usecases.create as createDomainUseCase
import com.briel.marnisos.brielapp.domain.calculations.ProposalCalculationHelper
import com.briel.marnisos.brielapp.domain.repository.AuthRepository
import com.briel.marnisos.brielapp.domain.repository.ConsumptionSessionRepository
import com.briel.marnisos.brielapp.domain.repository.ConsumptionStudyRepository
import com.briel.marnisos.brielapp.domain.repository.PriceUpdatesNotifier
import com.briel.marnisos.brielapp.domain.usecases.ClearCurrentUserConditionsUseCase
import com.briel.marnisos.brielapp.domain.usecases.ClearLastCompletedJobIdUseCase
import com.briel.marnisos.brielapp.domain.usecases.CalculateComparatorSummaryUseCase
import com.briel.marnisos.brielapp.domain.usecases.SelectUncompetitiveProposalsUseCase
import com.briel.marnisos.brielapp.domain.usecases.BuildCollectedPricesUseCase
import com.briel.marnisos.brielapp.domain.usecases.EvaluateFeeFirstGateUseCase
import com.briel.marnisos.brielapp.domain.usecases.ObserveFeeFirstGateUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetCurrentAuthUserUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetDeviceIdUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetFirebaseIdTokenUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetJobResultUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetJobStatusUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetLastCompletedJobIdUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetPriceTablesUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetUserConsumptionUseCase
import com.briel.marnisos.brielapp.domain.usecases.GenerateComparatorReportPdfUseCase
import com.briel.marnisos.brielapp.domain.usecases.ShouldCollectPricesUseCase
import com.briel.marnisos.brielapp.domain.usecases.SubmitCollectedPricesUseCase
import com.briel.marnisos.brielapp.domain.usecases.LoginWithEmailUseCase
import com.briel.marnisos.brielapp.domain.usecases.LoginWithGoogleUseCase
import com.briel.marnisos.brielapp.domain.usecases.LogoutUseCase
import com.briel.marnisos.brielapp.domain.usecases.SignOutLocallyUseCase
import com.briel.marnisos.brielapp.domain.usecases.ObserveCurrentUserConditionsUseCase
import com.briel.marnisos.brielapp.domain.usecases.IncrementProposalResponseCounterUseCase
import com.briel.marnisos.brielapp.domain.usecases.PersistCurrentUserConditionsUseCase
import com.briel.marnisos.brielapp.domain.usecases.PersistLastCompletedJobIdUseCase
import com.briel.marnisos.brielapp.domain.usecases.RefreshConsumptionReportUseCase
import com.briel.marnisos.brielapp.domain.usecases.SetUserOfflineUseCase
import com.briel.marnisos.brielapp.domain.usecases.SetUserOnlineUseCase
import com.briel.marnisos.brielapp.domain.usecases.SubmitConsumptionReportJobUseCase
import com.briel.marnisos.brielapp.domain.usecases.SubmitConsumptionReportByCupsJobUseCase
import com.briel.marnisos.brielapp.domain.usecases.SyncAuthenticatedUserDataUseCase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {
    // Application-scoped coroutine scope for work that must outlive a screen.
    single<CoroutineScope>(named(APPLICATION_SCOPE)) {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    // Provide a singleton HttpClient from KtorClientProvider
    single { KtorClientProvider.client }

    // Provide API implementation
    single { PriceTableApi(get()) }
    single { AuthApi(get()) }

    // Firebase
    single { FirebaseAuth.getInstance() }

    // Local persistence
    single { LastCompletedJobIdLocalDataSource(context = get<Context>()) }
    single { CurrentUserConditionsLocalDataSource(context = get<Context>()) }
    single { DeviceIdLocalDataSource(context = get<Context>()) }

    // Provide repository
    single<Repository> { buildRepository(get(), get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(firebaseAuth = get(), authApi = get()) }
    single<ConsumptionSessionRepository> { ConsumptionSessionRepositoryImpl() }
    single<PriceUpdatesNotifier> { PriceUpdatesNotifierImpl() }
    single<ConsumptionStudyRepository> {
        ConsumptionStudyRepositoryImpl(
            repository = get(),
            consumptionSessionRepository = get(),
            authRepository = get(),
            crashReporter = get(),
            applicationScope = get(named(APPLICATION_SCOPE)),
        )
    }

    // Provide use cases
    factory<GetPriceTablesUseCase> { GetPriceTablesUseCase.Factory.create(get()) }
    factory<GetUserConsumptionUseCase> { GetUserConsumptionUseCase.Factory.create(get()) }
    
    // Async job processing use cases
    factory<SubmitConsumptionReportJobUseCase> { SubmitConsumptionReportJobUseCase.Factory.create(get()) }
    factory<SubmitConsumptionReportByCupsJobUseCase> { SubmitConsumptionReportByCupsJobUseCase.Factory.create(get()) }
    factory<GetJobStatusUseCase> { GetJobStatusUseCase.Factory.create(get()) }
    factory<GetJobResultUseCase> { GetJobResultUseCase.Factory.create(get()) }
    factory<RefreshConsumptionReportUseCase> { RefreshConsumptionReportUseCase.Factory.create(get()) }
    factory<GenerateComparatorReportPdfUseCase> { GenerateComparatorReportPdfUseCase.Factory.create(get()) }
    factory<PersistLastCompletedJobIdUseCase> { PersistLastCompletedJobIdUseCase.Factory.create(get()) }
    factory<GetLastCompletedJobIdUseCase> { GetLastCompletedJobIdUseCase.Factory.create(get()) }
    factory<ClearLastCompletedJobIdUseCase> { ClearLastCompletedJobIdUseCase.Factory.create(get()) }
    factory<SetUserOnlineUseCase> { SetUserOnlineUseCase.Factory.create(get()) }
    factory<SetUserOfflineUseCase> { SetUserOfflineUseCase.Factory.create(get()) }
    factory<IncrementProposalResponseCounterUseCase> { IncrementProposalResponseCounterUseCase.Factory.create(get()) }
    factory<ClearCurrentUserConditionsUseCase> { ClearCurrentUserConditionsUseCase.Factory.create(get()) }
    factory<ObserveCurrentUserConditionsUseCase> { ObserveCurrentUserConditionsUseCase.Factory.create(get()) }
    factory<PersistCurrentUserConditionsUseCase> { PersistCurrentUserConditionsUseCase.Factory.create(get()) }

    // Collected prices
    factory<ShouldCollectPricesUseCase> { ShouldCollectPricesUseCase.Factory.createDomainUseCase() }
    factory<BuildCollectedPricesUseCase> { BuildCollectedPricesUseCase.Factory.createDomainUseCase() }
    factory<SubmitCollectedPricesUseCase> { SubmitCollectedPricesUseCase.Factory.create(get()) }

    // Fee-first gate
    factory<EvaluateFeeFirstGateUseCase> { EvaluateFeeFirstGateUseCase.Factory.createDomainUseCase() }
    single { ProposalCalculationHelper() }
    factory<SelectUncompetitiveProposalsUseCase> { SelectUncompetitiveProposalsUseCase() }
    factory<CalculateComparatorSummaryUseCase> {
        CalculateComparatorSummaryUseCase.Factory.createDomainUseCase(proposalCalculationHelper = get())
    }
    factory<ObserveFeeFirstGateUseCase> {
        ObserveFeeFirstGateUseCase.Factory.create(
            repository = get(),
            consumptionSessionRepository = get(),
            evaluateFeeFirstGateUseCase = get(),
        )
    }

    // Auth use cases
    factory<LoginWithEmailUseCase> { LoginWithEmailUseCase.Factory.create(get()) }
    factory<LoginWithGoogleUseCase> { LoginWithGoogleUseCase.Factory.create(get()) }
    factory<GetCurrentAuthUserUseCase> { GetCurrentAuthUserUseCase.Factory.create(get()) }
    factory<GetFirebaseIdTokenUseCase> { GetFirebaseIdTokenUseCase.Factory.create(get()) }
    factory<GetDeviceIdUseCase> { GetDeviceIdUseCase.Factory.create(get()) }
    factory<SyncAuthenticatedUserDataUseCase> { SyncAuthenticatedUserDataUseCase.Factory.create(get()) }
    factory<LogoutUseCase> { LogoutUseCase.Factory.create(get()) }
    factory<SignOutLocallyUseCase> { SignOutLocallyUseCase.Factory.create(get()) }
}

const val APPLICATION_SCOPE = "applicationScope"
