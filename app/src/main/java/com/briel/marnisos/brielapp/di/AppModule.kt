package com.briel.marnisos.brielapp.di

import com.briel.marnisos.brielapp.domain.monitoring.CrashReporter
import com.briel.marnisos.brielapp.monitoring.FirebaseCrashReporter
import com.briel.marnisos.brielapp.ui.views.AppShellViewModel
import com.briel.marnisos.brielapp.ui.views.auth.AuthViewModel
import com.briel.marnisos.brielapp.ui.views.configuration.ConfigurationViewModel
import com.briel.marnisos.brielapp.ui.views.currentuserconditions.CurrentUserConditionsViewModel
import com.briel.marnisos.brielapp.ui.views.fetchconsumption.FetchConsumptionViewModel
import com.briel.marnisos.brielapp.ui.views.pricetable.export.ComparatorPdfFileStore
import com.briel.marnisos.brielapp.ui.views.pricetable.export.ComparatorPdfShareManager
import com.briel.marnisos.brielapp.ui.views.proposals.ProposalsViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { FirebaseCrashlytics.getInstance() }
    single<CrashReporter> { FirebaseCrashReporter(crashlytics = get()) }
    single { ComparatorPdfFileStore(context = get()) }
    single { ComparatorPdfShareManager() }

    viewModel {
        AppShellViewModel(
            consumptionStudyRepository = get(),
            priceUpdatesNotifier = get(),
            observeFeeFirstGateUseCase = get(),
        )
    }

    viewModel {
        FetchConsumptionViewModel(
            consumptionStudyRepository = get(),
            analyticsTracker = get(),
        )
    }

    viewModel {
        CurrentUserConditionsViewModel(
            consumptionSessionRepository = get(),
            observeCurrentUserConditionsUseCase = get(),
            persistCurrentUserConditionsUseCase = get(),
            shouldCollectPricesUseCase = get(),
            buildCollectedPricesUseCase = get(),
            submitCollectedPricesUseCase = get(),
            observeFeeFirstGateUseCase = get(),
        )
    }

    viewModel {
        ProposalsViewModel(
            consumptionSessionRepository = get(),
            observeCurrentUserConditionsUseCase = get(),
            calculateComparatorSummaryUseCase = get(),
            selectUncompetitiveProposalsUseCase = get(),
            generateComparatorReportPdfUseCase = get(),
            comparatorPdfFileStore = get(),
            crashReporter = get(),
            analyticsTracker = get(),
        )
    }

    viewModel {
        ConfigurationViewModel(
            consumptionSessionRepository = get(),
            analyticsTracker = get(),
        )
    }

    viewModel {
        AuthViewModel(
            loginWithEmailUseCase = get(),
            loginWithGoogleUseCase = get(),
            getCurrentAuthUserUseCase = get(),
            getFirebaseIdTokenUseCase = get(),
            getDeviceIdUseCase = get(),
            syncAuthenticatedUserDataUseCase = get(),
            setUserOfflineUseCase = get(),
            clearCurrentUserConditionsUseCase = get(),
            clearLastCompletedJobIdUseCase = get(),
            logoutUseCase = get(),
            crashReporter = get(),
            analyticsTracker = get(),
        )
    }
}
