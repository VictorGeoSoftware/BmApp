package com.briel.marnisos.brielapp.di

import com.briel.marnisos.brielapp.ui.views.auth.AuthViewModel
import com.briel.marnisos.brielapp.ui.views.currentuserconditions.CurrentUserConditionsViewModel
import com.briel.marnisos.brielapp.ui.views.pricetable.ComparatorViewModel
import com.briel.marnisos.brielapp.ui.views.pricetable.ProposalCalculationHelper
import com.briel.marnisos.brielapp.ui.views.pricetable.export.AndroidComparatorPdfGenerator
import com.briel.marnisos.brielapp.ui.views.pricetable.export.ComparatorPdfDocumentDataFactory
import com.briel.marnisos.brielapp.ui.views.pricetable.export.ComparatorPdfGenerator
import com.briel.marnisos.brielapp.ui.views.pricetable.export.ComparatorPdfShareManager
import org.koin.core.module.dsl.viewModel

import org.koin.dsl.module

val appModule = module {
    single { ProposalCalculationHelper() }
    single { ComparatorPdfDocumentDataFactory() }
    single<ComparatorPdfGenerator> { AndroidComparatorPdfGenerator(context = get()) }
    single { ComparatorPdfShareManager() }

    viewModel {
        ComparatorViewModel(
            submitConsumptionReportJobUseCase = get(),
            getJobStatusUseCase = get(),
            getJobResultUseCase = get(),
            refreshConsumptionReportUseCase = get(),
            persistLastCompletedJobIdUseCase = get(),
            getLastCompletedJobIdUseCase = get(),
            clearLastCompletedJobIdUseCase = get(),
            clearCurrentUserConditionsUseCase = get(),
            observeCurrentUserConditionsUseCase = get(),
            proposalCalculationHelper = get(),
            comparatorPdfDocumentDataFactory = get(),
            comparatorPdfGenerator = get()
        )
    }

    viewModel {
        CurrentUserConditionsViewModel(
            observeCurrentUserConditionsUseCase = get(),
            persistCurrentUserConditionsUseCase = get(),
        )
    }

    viewModel {
        AuthViewModel(
            loginWithEmailUseCase = get(),
            getCurrentAuthUserUseCase = get(),
            getFirebaseIdTokenUseCase = get(),
            syncAuthenticatedUserDataUseCase = get()
        )
    }
}
