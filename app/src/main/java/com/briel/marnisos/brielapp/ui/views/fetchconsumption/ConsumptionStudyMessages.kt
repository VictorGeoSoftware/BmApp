package com.briel.marnisos.brielapp.ui.views.fetchconsumption

import androidx.annotation.StringRes
import com.briel.marnisos.brielapp.R
import com.briel.marnisos.brielapp.domain.models.ConsumptionStudyFailure
import com.briel.marnisos.brielapp.domain.models.ConsumptionStudyStep

/**
 * Maps domain study progress/failures to user-facing resources.
 *
 * Localisation lives in the UI layer, so the data layer never resolves strings.
 */
@StringRes
internal fun ConsumptionStudyStep.messageRes(): Int = when (this) {
    ConsumptionStudyStep.UPLOADING_BILL -> R.string.consumption_study_step_uploading_bill
    ConsumptionStudyStep.SUBMITTING_CUPS -> R.string.consumption_study_step_submitting_cups
    ConsumptionStudyStep.QUEUED -> R.string.consumption_study_step_queued
    ConsumptionStudyStep.PROCESSING -> R.string.consumption_study_step_processing
    ConsumptionStudyStep.FETCHING_RESULTS -> R.string.consumption_study_step_fetching_results
}

@StringRes
internal fun ConsumptionStudyFailure.messageRes(): Int = when (this) {
    ConsumptionStudyFailure.SUBMIT_FAILED -> R.string.consumption_study_error_submit
    ConsumptionStudyFailure.PROCESSING_FAILED -> R.string.consumption_study_error_processing
    ConsumptionStudyFailure.STATUS_CHECK_FAILED -> R.string.consumption_study_error_status
    ConsumptionStudyFailure.RESULT_FETCH_FAILED -> R.string.consumption_study_error_result
    ConsumptionStudyFailure.TIMEOUT -> R.string.consumption_study_error_timeout
}
