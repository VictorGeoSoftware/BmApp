package com.briel.marnisos.brielapp.ui.views.common

import android.content.Context
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
import java.io.File

@Composable
internal fun TopActionBar(
    isUploadingReport: Boolean,
    isGeneratingPdf: Boolean,
    showPrintButton: Boolean,
    onGeneratePdfClick: () -> Unit,
    onPdfSelected: (File) -> Unit,
    context: Context,
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
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (showPrintButton) {
                PrintButton(
                    isGeneratingPdf = isGeneratingPdf,
                    onGeneratePdfClick = onGeneratePdfClick
                )
            }

            SelectFileButton(
                isUploadingReport = isUploadingReport,
                onPdfSelected = onPdfSelected,
                context = context,
            )
        }
    }
}
