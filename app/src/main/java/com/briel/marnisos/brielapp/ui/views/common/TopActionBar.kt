package com.briel.marnisos.brielapp.ui.views.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Top bar of the authenticated shell.
 *
 * Only the report action lives here, and only on the proposals screen. The scan
 * shortcut was removed: the fetch-consumption screen already offers scanning as an
 * explicit choice, so the duplicate in the top bar was noise (supersedes rule R7).
 */
@Composable
internal fun TopActionBar(
    isGeneratingPdf: Boolean,
    showPrintButton: Boolean,
    onGeneratePdfClick: () -> Unit,
    onOpenDrawer: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onOpenDrawer) {
            Text(
                text = "☰",
                style = MaterialTheme.typography.headlineSmall,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (showPrintButton) {
                PrintButton(
                    isGeneratingPdf = isGeneratingPdf,
                    onGeneratePdfClick = onGeneratePdfClick,
                )
            }
        }
    }
}
