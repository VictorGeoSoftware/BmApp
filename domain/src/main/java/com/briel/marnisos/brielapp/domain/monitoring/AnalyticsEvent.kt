package com.briel.marnisos.brielapp.domain.monitoring

/**
 * Analytics events emitted by the app.
 *
 * PRIVACY CONTRACT — parameters must only ever contain non-identifying values:
 * enums/fixed vocabularies, counts, durations and booleans.
 *
 * It is forbidden to put CUPS codes, customer names, addresses, e-mail addresses,
 * contract numbers, raw consumption values, OCR text or API error bodies into an
 * event. Analytics events are not deletable per user once collected.
 */
sealed class AnalyticsEvent(
    val name: String,
    val params: Map<String, Any> = emptyMap(),
) {

    // --- Authentication -------------------------------------------------

    data object LoginStarted : AnalyticsEvent("login_started")

    data object LoginSucceeded : AnalyticsEvent("login_succeeded")

    data class LoginFailed(val reason: AnalyticsFailureReason) : AnalyticsEvent(
        name = "login_failed",
        params = mapOf(PARAM_REASON to reason.value),
    )

    data object LoggedOut : AnalyticsEvent("logged_out")

    // --- CUPS scanner ---------------------------------------------------

    data object CupsScanStarted : AnalyticsEvent("cups_scan_started")

    data class CupsScanSucceeded(val durationMs: Long) : AnalyticsEvent(
        name = "cups_scan_succeeded",
        params = mapOf(PARAM_DURATION_MS to durationMs),
    )

    data class CupsScanFailed(val reason: AnalyticsFailureReason) : AnalyticsEvent(
        name = "cups_scan_failed",
        params = mapOf(PARAM_REASON to reason.value),
    )

    // --- Consumption ----------------------------------------------------

    data object ConsumptionFetchStarted : AnalyticsEvent("consumption_fetch_started")

    data class ConsumptionFetchSucceeded(val durationMs: Long) : AnalyticsEvent(
        name = "consumption_fetch_succeeded",
        params = mapOf(PARAM_DURATION_MS to durationMs),
    )

    data class ConsumptionFetchFailed(val reason: AnalyticsFailureReason) : AnalyticsEvent(
        name = "consumption_fetch_failed",
        params = mapOf(PARAM_REASON to reason.value),
    )

    // --- Proposals ------------------------------------------------------

    data class ProposalsGenerated(val proposalCount: Int) : AnalyticsEvent(
        name = "proposals_generated",
        params = mapOf(PARAM_ITEM_COUNT to proposalCount.toLong()),
    )

    data class ProposalPdfExported(val proposalCount: Int) : AnalyticsEvent(
        name = "proposal_pdf_exported",
        params = mapOf(PARAM_ITEM_COUNT to proposalCount.toLong()),
    )

    data class ProposalPdfExportFailed(val reason: AnalyticsFailureReason) : AnalyticsEvent(
        name = "proposal_pdf_export_failed",
        params = mapOf(PARAM_REASON to reason.value),
    )

    // --- Configuration --------------------------------------------------

    data class ProposalVisibilityChanged(val visibleCount: Int) : AnalyticsEvent(
        name = "proposal_visibility_changed",
        params = mapOf(PARAM_ITEM_COUNT to visibleCount.toLong()),
    )

    companion object {
        const val PARAM_REASON = "reason"
        const val PARAM_DURATION_MS = "duration_ms"
        const val PARAM_ITEM_COUNT = "item_count"
    }
}
