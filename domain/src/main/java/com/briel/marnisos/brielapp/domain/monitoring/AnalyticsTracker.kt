package com.briel.marnisos.brielapp.domain.monitoring

/**
 * App-wide analytics sink. Implemented in `:analytics`.
 *
 * Mirrors [CrashReporter]: the contract lives in the domain layer so any layer can
 * emit events without depending on Firebase or on Android.
 */
interface AnalyticsTracker {

    /** Enables or disables collection at runtime. Disabled by default. */
    fun setCollectionEnabled(enabled: Boolean)

    /** Records a screen view. [screenName] must be a stable snake_case identifier. */
    fun trackScreen(screenName: String)

    /** Records a user action or outcome. */
    fun track(event: AnalyticsEvent)
}
