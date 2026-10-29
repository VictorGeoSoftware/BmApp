package com.briel.marnisos.brielapp.ui.views.proposals

import androidx.annotation.StringRes

/** Snackbar message of the comprehensive report; Retry is offered only when [canRetry]. */
data class ComprehensiveReportMessage(
    @StringRes val messageRes: Int,
    val canRetry: Boolean,
)
