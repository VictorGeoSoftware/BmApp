package com.briel.marnisos.brielapp.ui.views.common

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.briel.marnisos.brielapp.R

/**
 * Generates the PDF report. Labelled rather than an icon: a save glyph did not say what
 * it produced, and this is the closing action of the whole flow.
 */
@Composable
internal fun PrintButton(
    isGeneratingPdf: Boolean,
    onGeneratePdfClick: () -> Unit,
) {
    TextButton(
        onClick = onGeneratePdfClick,
        enabled = !isGeneratingPdf,
    ) {
        if (isGeneratingPdf) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp))
        } else {
            Text(
                text = stringResource(R.string.proposals_create_report),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
