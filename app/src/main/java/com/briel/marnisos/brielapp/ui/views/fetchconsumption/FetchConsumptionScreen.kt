package com.briel.marnisos.brielapp.ui.views.fetchconsumption

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.briel.marnisos.brielapp.R
import com.briel.marnisos.brielapp.domain.models.ConsumptionStudyStep
import com.briel.marnisos.brielapp.ui.Utils.uriToFile
import com.briel.marnisos.brielapp.ui.components.ChevronRightIcon
import com.briel.marnisos.brielapp.ui.components.FileUploadIcon
import com.briel.marnisos.brielapp.ui.components.ScanIcon
import com.briel.marnisos.brielapp.ui.theme.BrielAppTheme
import com.briel.marnisos.brielapp.ui.theme.extendedColors
import org.koin.androidx.compose.koinViewModel
import java.io.File

/**
 * First mandatory step of the fee-first flow: without a consumption study there is
 * nothing to price, so this is the only reachable destination.
 */
@Composable
internal fun FetchConsumptionScreen(
    onScanCupsSelected: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FetchConsumptionViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        val pdfFile: File? = uri?.let { selectedUri -> uriToFile(context, selectedUri) }
        pdfFile?.let(viewModel::onPdfSelected)
    }

    FetchConsumptionContent(
        uiState = uiState,
        onScanCupsSelected = onScanCupsSelected,
        onUploadPdfSelected = { pdfPickerLauncher.launch(arrayOf(PDF_MIME_TYPE)) },
        modifier = modifier,
    )
}

@Composable
private fun FetchConsumptionContent(
    uiState: FetchConsumptionUiState,
    onScanCupsSelected: () -> Unit,
    onUploadPdfSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = extendedColors

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (uiState.isStudyRunning) {
            StudyInProgressCard(step = uiState.step)
            return@Column
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = stringResource(R.string.fetch_consumption_headline),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.tableText,
            )
            Text(
                text = stringResource(R.string.fetch_consumption_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = colors.tableTextSecondary,
            )
        }

        Text(
            text = stringResource(R.string.fetch_consumption_method_label),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = colors.tableTextSecondary,
        )

        ConsumptionMethodOption(
            icon = ScanIcon,
            title = stringResource(R.string.fetch_consumption_scan_title),
            subtitle = stringResource(R.string.fetch_consumption_scan_subtitle),
            onClick = onScanCupsSelected,
        )

        ConsumptionMethodOption(
            icon = FileUploadIcon,
            title = stringResource(R.string.fetch_consumption_upload_title),
            subtitle = stringResource(R.string.fetch_consumption_upload_subtitle),
            onClick = onUploadPdfSelected,
        )

        uiState.failure?.let { failure ->
            Text(
                text = stringResource(failure.messageRes()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun ConsumptionMethodOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    val colors = extendedColors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.headerHighlight),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.tableText,
                modifier = Modifier.size(20.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.tableText,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.tableTextSecondary,
            )
        }

        Icon(
            imageVector = ChevronRightIcon,
            contentDescription = null,
            tint = colors.tableTextSecondary,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun StudyInProgressCard(step: ConsumptionStudyStep?) {
    val colors = extendedColors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)

        Text(
            text = stringResource(R.string.fetch_consumption_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = colors.tableText,
        )

        step?.let {
            Text(
                text = stringResource(it.messageRes()),
                style = MaterialTheme.typography.bodySmall,
                color = colors.tableTextSecondary,
            )
        }
    }
}

private const val PDF_MIME_TYPE = "application/pdf"

@Preview(name = "Idle", showBackground = true)
@Composable
private fun FetchConsumptionIdlePreview() {
    BrielAppTheme(darkTheme = false) {
        FetchConsumptionContent(
            uiState = FetchConsumptionUiState(),
            onScanCupsSelected = {},
            onUploadPdfSelected = {},
        )
    }
}

@Preview(name = "Running", showBackground = true)
@Composable
private fun FetchConsumptionRunningPreview() {
    BrielAppTheme(darkTheme = false) {
        FetchConsumptionContent(
            uiState = FetchConsumptionUiState(
                isStudyRunning = true,
                step = ConsumptionStudyStep.PROCESSING,
            ),
            onScanCupsSelected = {},
            onUploadPdfSelected = {},
        )
    }
}
