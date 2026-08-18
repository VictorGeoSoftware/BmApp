package com.briel.marnisos.brielapp.analytics

import android.os.Bundle
import com.briel.marnisos.brielapp.domain.monitoring.AnalyticsEvent
import com.briel.marnisos.brielapp.domain.monitoring.AnalyticsFailureReason
import com.google.firebase.analytics.FirebaseAnalytics
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FirebaseAnalyticsTrackerTest {

    private val firebaseAnalytics: FirebaseAnalytics = mockk(relaxed = true)
    private val tracker = FirebaseAnalyticsTracker(firebaseAnalytics)

    @Before
    fun setUp() {
        // android.os.Bundle is stubbed in unit tests; back it with a real map so the
        // assertions below observe what was actually written.
        mockkStatic(Bundle::class)
    }

    @Test
    fun `screen view is logged under the firebase screen_view event`() {
        val nameSlot = slot<String>()

        tracker.trackScreen("proposals")

        verify { firebaseAnalytics.logEvent(capture(nameSlot), any()) }
        assertEquals(FirebaseAnalytics.Event.SCREEN_VIEW, nameSlot.captured)
    }

    @Test
    fun `event name is forwarded verbatim when already valid`() {
        val nameSlot = slot<String>()

        tracker.track(AnalyticsEvent.LoginSucceeded)

        verify { firebaseAnalytics.logEvent(capture(nameSlot), any()) }
        assertEquals("login_succeeded", nameSlot.captured)
    }

    @Test
    fun `every declared event name satisfies the firebase constraints`() {
        val events = listOf(
            AnalyticsEvent.LoginStarted,
            AnalyticsEvent.LoginSucceeded,
            AnalyticsEvent.LoginFailed(AnalyticsFailureReason.NETWORK),
            AnalyticsEvent.LoggedOut,
            AnalyticsEvent.CupsScanStarted,
            AnalyticsEvent.CupsScanSucceeded(durationMs = 1),
            AnalyticsEvent.CupsScanFailed(AnalyticsFailureReason.PERMISSION_DENIED),
            AnalyticsEvent.ConsumptionFetchStarted,
            AnalyticsEvent.ConsumptionFetchSucceeded(durationMs = 1),
            AnalyticsEvent.ConsumptionFetchFailed(AnalyticsFailureReason.TIMEOUT),
            AnalyticsEvent.ProposalsGenerated(proposalCount = 3),
            AnalyticsEvent.ProposalPdfExported(proposalCount = 3),
            AnalyticsEvent.ProposalPdfExportFailed(AnalyticsFailureReason.UNKNOWN),
            AnalyticsEvent.ProposalVisibilityChanged(visibleCount = 2),
        )

        events.forEach { event ->
            assertTrue(
                "'${event.name}' exceeds the 40 character limit",
                event.name.length <= MAX_EVENT_NAME_LENGTH,
            )
            assertTrue(
                "'${event.name}' must be lower snake_case starting with a letter",
                event.name.matches(SNAKE_CASE),
            )
            assertTrue(
                "'${event.name}' declares more than 25 parameters",
                event.params.size <= MAX_PARAM_COUNT,
            )
        }
    }

    @Test
    fun `no event carries a free-text parameter value`() {
        // The privacy contract: string values may only be fixed-vocabulary reasons.
        val allowedStrings = AnalyticsFailureReason.entries.map { it.value }.toSet()
        val events = listOf(
            AnalyticsEvent.LoginFailed(AnalyticsFailureReason.UNAUTHORIZED),
            AnalyticsEvent.CupsScanFailed(AnalyticsFailureReason.CANCELLED),
            AnalyticsEvent.ConsumptionFetchFailed(AnalyticsFailureReason.PARSE_ERROR),
            AnalyticsEvent.ProposalPdfExportFailed(AnalyticsFailureReason.NETWORK),
        )

        events.flatMap { it.params.values }
            .filterIsInstance<String>()
            .forEach { value ->
                assertTrue("Unexpected free-text value '$value'", value in allowedStrings)
            }
    }

    @Test
    fun `collection toggle is delegated to firebase`() {
        tracker.setCollectionEnabled(true)

        verify { firebaseAnalytics.setAnalyticsCollectionEnabled(true) }
    }

    private companion object {
        const val MAX_EVENT_NAME_LENGTH = 40
        const val MAX_PARAM_COUNT = 25
        val SNAKE_CASE = Regex("^[a-z][a-z0-9_]*$")
    }
}
