package com.briel.marnisos.brielapp.domain.models

/**
 * Steps reported while a consumption study is running.
 *
 * Kept as a domain enum rather than a user-facing string so the UI layer owns
 * localisation (see AGENTS.md: ViewModels must not resolve resources).
 */
enum class ConsumptionStudyStep {
    UPLOADING_BILL,
    SUBMITTING_CUPS,
    QUEUED,
    PROCESSING,
    FETCHING_RESULTS,
}
