package com.briel.marnisos.brielapp.ui.views.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.briel.marnisos.brielapp.R

/**
 * Comprehensive (Premium) report action. Shown only to capability holders and disabled
 * until the export feature lands: tapping it does nothing (no request, no navigation).
 */
@Composable
internal fun ComprehensiveReportButton() {
    TextButton(
        onClick = {},
        enabled = false,
    ) {
        Text(
            text = stringResource(R.string.proposals_comprehensive_report),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
