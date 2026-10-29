package com.briel.marnisos.brielapp.ui.views.common

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.briel.marnisos.brielapp.R

/**
 * Comprehensive (Premium) report action. Shown only to capability holders; disabled
 * with a progress indicator while a request is in flight (one request at a time).
 */
@Composable
internal fun ComprehensiveReportButton(
    isGenerating: Boolean,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        enabled = !isGenerating,
    ) {
        if (isGenerating) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp))
        } else {
            Text(
                text = stringResource(R.string.proposals_comprehensive_report),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
