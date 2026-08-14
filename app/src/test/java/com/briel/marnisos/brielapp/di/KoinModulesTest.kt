package com.briel.marnisos.brielapp.di

import android.content.Context
import com.briel.marnisos.brielapp.data.di.dataModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module
import org.koin.test.check.checkModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

/**
 * Guards the Koin graph at build time.
 *
 * Without this, a missing or mistyped binding only surfaces as a crash when the
 * screen is first opened on a device. This instantiates every definition in
 * [dataModule] and [appModule], so an unresolvable dependency fails the build.
 *
 * Platform-bound singletons (Firebase, Android [Context]) are substituted with
 * mocks — the point is to verify the wiring, not the SDKs.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class KoinModulesTest {

    private val platformOverrides = module {
        single<FirebaseCrashlytics> { mockk(relaxed = true) }
        single<FirebaseAuth> { mockk(relaxed = true) }
    }

    @Before
    fun setUp() {
        // ViewModels start collecting in viewModelScope on construction.
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun `every definition in the graph resolves`() {
        startKoin {
            modules(dataModule, appModule, platformOverrides)
        }.checkModules {
            withInstance<Context>(mockk(relaxed = true))
        }
    }
}
