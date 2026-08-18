package com.briel.marnisos.brielapp.analytics.di

import com.briel.marnisos.brielapp.analytics.AnalyticsCollectionPolicy
import com.briel.marnisos.brielapp.analytics.FirebaseAnalyticsTracker
import com.briel.marnisos.brielapp.analytics.NoOpAnalyticsTracker
import com.briel.marnisos.brielapp.domain.monitoring.AnalyticsTracker
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val analyticsModule = module {
    single<AnalyticsTracker> {
        if (AnalyticsCollectionPolicy.isCollectionAllowed) {
            FirebaseAnalyticsTracker(FirebaseAnalytics.getInstance(androidContext()))
        } else {
            NoOpAnalyticsTracker
        }
    }
}
