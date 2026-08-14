package com.briel.marnisos.brielapp.domain.models

/**
 * Reasons a consumption study can fail, so the UI can localise the message.
 */
enum class ConsumptionStudyFailure {
    SUBMIT_FAILED,
    PROCESSING_FAILED,
    STATUS_CHECK_FAILED,
    RESULT_FETCH_FAILED,
    TIMEOUT,
}
