package com.briel.marnisos.brielapp.di

import com.briel.marnisos.brielapp.ui.views.auth.AuthViewModel
import com.briel.marnisos.brielapp.ui.views.pricetable.ComparatorViewModel
import org.koin.core.module.dsl.viewModel

import org.koin.dsl.module

val appModule = module {
    viewModel {
        ComparatorViewModel(
            submitConsumptionReportJobUseCase = get(),
            getJobStatusUseCase = get(),
            getJobResultUseCase = get(),
            refreshConsumptionReportUseCase = get()
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
