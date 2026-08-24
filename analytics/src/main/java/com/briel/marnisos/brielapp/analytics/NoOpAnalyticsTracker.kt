package com.briel.marnisos.brielapp.analytics

import com.briel.marnisos.brielapp.domain.monitoring.AnalyticsEvent
import com.briel.marnisos.brielapp.domain.monitoring.AnalyticsTracker

/**
 * Used on builds that are not allowed to report analytics (local/dev, and any
 * non-release build type). Guarantees no Firebase SDK call is ever made there.
 */
object NoOpAnalyticsTracker : AnalyticsTracker {
    override fun setCollectionEnabled(enabled: Boolean) = Unit
    override fun trackScreen(screenName: String) = Unit
    override fun track(event: AnalyticsEvent) = Unit
}
