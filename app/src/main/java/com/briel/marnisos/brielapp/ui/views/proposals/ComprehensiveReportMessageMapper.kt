package com.briel.marnisos.brielapp.ui.views.proposals

import com.briel.marnisos.brielapp.R
import com.briel.marnisos.brielapp.domain.models.PremiumReportFailure

/** Truth table of docs/specs/report-android-export.md: one fixed message per failure. */
internal fun PremiumReportFailure.toComprehensiveReportMessage(): ComprehensiveReportMessage = when (this) {
    PremiumReportFailure.PREMIUM_REQUIRED -> ComprehensiveReportMessage(R.string.premium_report_required, false)
    PremiumReportFailure.ACCESS_REVOKED -> ComprehensiveReportMessage(R.string.premium_report_access_revoked, false)
    PremiumReportFailure.UNAUTHENTICATED -> ComprehensiveReportMessage(R.string.premium_report_generation_failed, true)
    PremiumReportFailure.STUDY_NOT_FOUND -> ComprehensiveReportMessage(R.string.premium_report_study_expired, false)
    PremiumReportFailure.STUDY_NOT_READY -> ComprehensiveReportMessage(R.string.premium_report_study_not_ready, true)
    PremiumReportFailure.HOLDER_MISSING -> ComprehensiveReportMessage(R.string.premium_report_holder_missing, false)
    PremiumReportFailure.ADDRESS_MISSING -> ComprehensiveReportMessage(R.string.premium_report_address_missing, false)
    PremiumReportFailure.CONDITIONS_MISSING -> ComprehensiveReportMessage(R.string.premium_report_conditions_missing, false)
    PremiumReportFailure.DATA_MISMATCH -> ComprehensiveReportMessage(R.string.premium_report_data_mismatch, true)
    PremiumReportFailure.UNAVAILABLE -> ComprehensiveReportMessage(R.string.premium_report_unavailable, false)
    PremiumReportFailure.GENERATION_FAILED -> ComprehensiveReportMessage(R.string.premium_report_generation_failed, true)
}
