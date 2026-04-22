package com.briel.marnisos.brielapp.di

import com.briel.marnisos.brielapp.ui.views.auth.AuthViewModel
import com.briel.marnisos.brielapp.ui.views.currentuserconditions.CurrentUserConditionsViewModel
import com.briel.marnisos.brielapp.ui.views.pricetable.ComparatorViewModel
import com.briel.marnisos.brielapp.ui.views.pricetable.ProposalCalculationHelper
import com.briel.marnisos.brielapp.ui.views.pricetable.export.ComparatorPdfFileStore
import com.briel.marnisos.brielapp.ui.views.pricetable.export.ComparatorPdfShareManager
import org.koin.core.module.dsl.viewModel

import org.koin.dsl.module

val appModule = module {
    single { ProposalCalculationHelper() }
    single { ComparatorPdfFileStore(context = get()) }
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
            getCurrentAuthUserUseCase = get(),
            incrementProposalResponseCounterUseCase = get(),
            generateComparatorReportPdfUseCase = get(),
            proposalCalculationHelper = get(),
            comparatorPdfFileStore = get()
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
