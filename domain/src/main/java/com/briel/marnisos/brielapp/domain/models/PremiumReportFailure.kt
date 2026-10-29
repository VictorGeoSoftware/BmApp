package com.briel.marnisos.brielapp.domain.models

/**
 * Why a comprehensive (Premium) report could not be produced. Chosen from the HTTP
 * status and the backend error code only; server message text is never carried.
 */
enum class PremiumReportFailure {
    PREMIUM_REQUIRED,
    ACCESS_REVOKED,
    UNAUTHENTICATED,
    STUDY_NOT_FOUND,
    STUDY_NOT_READY,
    HOLDER_MISSING,
    ADDRESS_MISSING,
    CONDITIONS_MISSING,
    DATA_MISMATCH,
    UNAVAILABLE,
    GENERATION_FAILED,
}
