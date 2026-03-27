package com.briel.marnisos.brielapp.ui.views.common

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.briel.marnisos.brielapp.ui.Utils.uriToFile
import java.io.File

@Composable
internal fun SelectFileButton(
    isUploadingReport: Boolean = false,
    onPdfSelected: (File) -> Unit = {},
    context: Context,
) {
    val colorScheme = MaterialTheme.colorScheme

    val fileButtonColors = ButtonDefaults.buttonColors(
        containerColor = colorScheme.primary,
        contentColor = colorScheme.onPrimary,
        disabledContainerColor = colorScheme.primary.copy(alpha = 0.8f),
        disabledContentColor = colorScheme.onPrimary
    )

    // PDF file picker launcher
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            // Convert URI to File
            val file = uriToFile(context, it)
            file?.let { pdfFile ->
                onPdfSelected(pdfFile)
            }
        }
    }

    Button(
        onClick = {
            pdfPickerLauncher.launch(arrayOf("application/pdf"))
        },
        enabled = !isUploadingReport,
        colors = fileButtonColors
    ) {
        if (isUploadingReport) {
            CircularProgressIndicator(
                modifier = Modifier.padding(end = 8.dp),
                color = colorScheme.onPrimary
            )
        }
        Text(if (isUploadingReport) "Procesando..." else "Selecciona una factura")
    }
}
