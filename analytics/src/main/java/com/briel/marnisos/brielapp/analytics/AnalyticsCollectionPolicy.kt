package com.briel.marnisos.brielapp.analytics

/**
 * Decides whether this build may report analytics at all.
 *
 * Mirrors the Crashlytics gate in `BrielApp.configureCrashMonitoring()`: production
 * release builds only. When collection is not allowed the DI graph binds
 * [NoOpAnalyticsTracker], so the Firebase SDK is never touched.
 */
object AnalyticsCollectionPolicy {

    val isCollectionAllowed: Boolean
        get() = BuildConfig.ANALYTICS_COLLECTION_ALLOWED &&
            (BuildConfig.ANALYTICS_ALLOW_DEBUG_BUILDS || BuildConfig.BUILD_TYPE == RELEASE_BUILD_TYPE)

    private const val RELEASE_BUILD_TYPE = "release"
}
