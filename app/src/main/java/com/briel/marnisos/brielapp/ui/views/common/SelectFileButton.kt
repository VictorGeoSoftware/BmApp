package com.briel.marnisos.brielapp.ui.views.common

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.briel.marnisos.brielapp.ui.Utils.uriToFile
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SelectFileButton(
    isUploadingReport: Boolean = false,
    onPdfSelected: (File) -> Unit = {},
    onScanCupsSelected: () -> Unit = {},
    context: Context,
) {
    val colorScheme = MaterialTheme.colorScheme
    var showOptionsSheet by remember { mutableStateOf(false) }

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
            showOptionsSheet = true
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
        Text(if (isUploadingReport) "Procesando..." else "Comparar")
    }

    if (showOptionsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showOptionsSheet = false },
        ) {
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Text(
                    text = "¿Cómo quieres comparar?",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
                ListItem(
                    headlineContent = { Text("Seleccionar una factura") },
                    supportingContent = { Text("Sube un PDF de la factura") },
                    modifier = Modifier.fillMaxWidth(),
                    overlineContent = null,
                    trailingContent = null,
                    leadingContent = null,
                )
                Button(
                    onClick = {
                        showOptionsSheet = false
                        pdfPickerLauncher.launch(arrayOf("application/pdf"))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                ) {
                    Text("Seleccionar una factura")
                }

                Button(
                    onClick = {
                        showOptionsSheet = false
                        onScanCupsSelected()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                ) {
                    Text("Escanear CUPS")
                }
            }
        }
    }
}
