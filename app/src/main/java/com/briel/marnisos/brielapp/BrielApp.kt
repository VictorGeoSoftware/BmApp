package com.briel.marnisos.brielapp

import android.app.Application
import com.briel.marnisos.brielapp.data.di.dataModule
import com.briel.marnisos.brielapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BrielApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BrielApp)
            modules(listOf(dataModule, appModule))
        }
    }
}
