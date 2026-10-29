package com.briel.marnisos.brielapp.ui.views.common

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.briel.marnisos.brielapp.R
import com.briel.marnisos.brielapp.ui.views.proposals.ComprehensiveReportMessage

/**
 * Shows a comprehensive-report message as a snackbar. Retry is offered only when the
 * message allows it; otherwise the snackbar has no action and simply times out.
 */
@Composable
internal fun ComprehensiveReportSnackbarEffect(
    message: ComprehensiveReportMessage?,
    snackbarHostState: SnackbarHostState,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val currentOnRetry by rememberUpdatedState(onRetry)
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    LaunchedEffect(message) {
        if (message == null) return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(
            message = context.getString(message.messageRes),
            actionLabel = if (message.canRetry) context.getString(R.string.premium_report_retry) else null,
            duration = SnackbarDuration.Long,
        )
        if (result == SnackbarResult.ActionPerformed) currentOnRetry() else currentOnDismiss()
    }
}
