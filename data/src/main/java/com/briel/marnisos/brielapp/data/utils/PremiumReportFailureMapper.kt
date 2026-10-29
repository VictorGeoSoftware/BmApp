package com.briel.marnisos.brielapp.data.utils

import com.briel.marnisos.brielapp.domain.models.PremiumReportFailure

/** Truth table A7–A17 (docs/specs/report-android-export.md): status + code only. */
object PremiumReportFailureMapper {

    fun fromResponse(status: Int, code: String?): PremiumReportFailure = when (status) {
        401 -> PremiumReportFailure.UNAUTHENTICATED
        403 -> if (code == "premium_required") {
            PremiumReportFailure.PREMIUM_REQUIRED
        } else {
            PremiumReportFailure.ACCESS_REVOKED
        }
        404 -> code.expect("study_not_found", PremiumReportFailure.STUDY_NOT_FOUND)
        409 -> code.expect("study_not_ready", PremiumReportFailure.STUDY_NOT_READY)
        400 -> code.expect("invalid_request", PremiumReportFailure.DATA_MISMATCH)
        501 -> code.expect("report_unavailable", PremiumReportFailure.UNAVAILABLE)
        422 -> when (code) {
            null -> PremiumReportFailure.GENERATION_FAILED
            "holder_missing" -> PremiumReportFailure.HOLDER_MISSING
            "address_missing" -> PremiumReportFailure.ADDRESS_MISSING
            "current_conditions_missing" -> PremiumReportFailure.CONDITIONS_MISSING
            else -> PremiumReportFailure.DATA_MISMATCH
        }
        else -> PremiumReportFailure.GENERATION_FAILED
    }

    private fun String?.expect(expected: String, failure: PremiumReportFailure): PremiumReportFailure =
        if (this == expected) failure else PremiumReportFailure.GENERATION_FAILED
}
