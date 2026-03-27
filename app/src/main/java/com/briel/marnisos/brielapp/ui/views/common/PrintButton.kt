package com.briel.marnisos.brielapp.ui.views.common

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource

@Composable
internal fun PrintButton(
    isGeneratingPdf: Boolean,
    onGeneratePdfClick: () -> Unit,
) {
    IconButton(
        onClick = onGeneratePdfClick,
        enabled = !isGeneratingPdf
    ) {
        if (isGeneratingPdf) {
            CircularProgressIndicator()
        } else {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_save),
                contentDescription = "Generar PDF"
            )
        }
    }
}