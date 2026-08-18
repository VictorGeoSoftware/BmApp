package com.briel.marnisos.brielapp.analytics

import android.os.Bundle
import com.briel.marnisos.brielapp.domain.monitoring.AnalyticsEvent
import com.briel.marnisos.brielapp.domain.monitoring.AnalyticsTracker
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Firebase-backed [AnalyticsTracker].
 *
 * Collection is off until [setCollectionEnabled] is called, so nothing is sent
 * before the app has decided whether this build is allowed to report.
 */
class FirebaseAnalyticsTracker(
    private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsTracker {

    override fun setCollectionEnabled(enabled: Boolean) {
        firebaseAnalytics.setAnalyticsCollectionEnabled(enabled)
    }

    override fun trackScreen(screenName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName.sanitizeName(MAX_VALUE_LENGTH))
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    override fun track(event: AnalyticsEvent) {
        firebaseAnalytics.logEvent(
            event.name.sanitizeName(MAX_EVENT_NAME_LENGTH),
            event.params.toBundle(),
        )
    }

    private fun Map<String, Any>.toBundle(): Bundle {
        val bundle = Bundle()
        entries.take(MAX_PARAM_COUNT).forEach { (key, value) ->
            val name = key.sanitizeName(MAX_PARAM_NAME_LENGTH)
            when (value) {
                is String -> bundle.putString(name, value.take(MAX_VALUE_LENGTH))
                is Int -> bundle.putLong(name, value.toLong())
                is Long -> bundle.putLong(name, value)
                is Double -> bundle.putDouble(name, value)
                is Float -> bundle.putDouble(name, value.toDouble())
                is Boolean -> bundle.putLong(name, if (value) 1L else 0L)
                // Unsupported types are dropped rather than stringified: toString() on an
                // arbitrary object is exactly how PII leaks into analytics.
                else -> Unit
            }
        }
        return bundle
    }

    /**
     * Firebase rejects names that are not alphanumeric/underscore, that do not start
     * with a letter, or that exceed the length limit.
     */
    private fun String.sanitizeName(maxLength: Int): String =
        map { if (it.isLetterOrDigit() || it == '_') it else '_' }
            .joinToString("")
            .let { if (it.firstOrNull()?.isLetter() == true) it else "e_$it" }
            .take(maxLength)

    private companion object {
        const val MAX_EVENT_NAME_LENGTH = 40
        const val MAX_PARAM_NAME_LENGTH = 40
        const val MAX_PARAM_COUNT = 25
        const val MAX_VALUE_LENGTH = 100
    }
}
