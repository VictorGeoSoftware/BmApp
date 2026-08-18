package com.briel.marnisos.brielapp.domain.monitoring

/**
 * Fixed vocabulary of failure reasons attached to analytics events.
 *
 * Deliberately coarse: a reason must never carry a server message, a stack trace
 * or any user-supplied text. Diagnostics belong in Crashlytics, not analytics.
 */
enum class AnalyticsFailureReason(val value: String) {
    NETWORK("network"),
    TIMEOUT("timeout"),
    UNAUTHORIZED("unauthorized"),
    NOT_FOUND("not_found"),
    INVALID_INPUT("invalid_input"),
    PERMISSION_DENIED("permission_denied"),
    CANCELLED("cancelled"),
    PARSE_ERROR("parse_error"),
    UNKNOWN("unknown"),
}
