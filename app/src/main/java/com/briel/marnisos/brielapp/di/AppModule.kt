package com.briel.marnisos.brielapp.di

import com.briel.marnisos.brielapp.ui.views.pricetable.PriceTableViewModel
import org.koin.core.module.dsl.viewModel

import org.koin.dsl.module

val appModule = module {
    viewModel { PriceTableViewModel(get()) }
}
