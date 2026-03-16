package com.briel.marnisos.brielapp.ui.views

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices.PIXEL_TABLET
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel
import com.briel.marnisos.brielapp.ui.Utils.uriToFile
import com.briel.marnisos.brielapp.ui.components.tables.DynamicTableColumnView
import com.briel.marnisos.brielapp.ui.components.tables.SideTitleTableView
import com.briel.marnisos.brielapp.ui.theme.AppOnPrimary
import com.briel.marnisos.brielapp.ui.theme.AppPrimary
import com.briel.marnisos.brielapp.ui.theme.Corner
import com.briel.marnisos.brielapp.ui.theme.HeaderYellow
import com.briel.marnisos.brielapp.ui.views.common.HeaderBox
import com.briel.marnisos.brielapp.ui.views.common.SectionHeader
import com.briel.marnisos.brielapp.ui.views.pricetable.ComparatorViewModel
import com.briel.marnisos.brielapp.ui.views.pricetable.PriceProposalColumn
import org.koin.androidx.compose.koinViewModel
import java.io.File

/**
 * ComparatorView — A Compose-only UI that mirrors the provided mockup.
 * Focused on the View layer; can be fed with a UI model from upper layers later.
 */
@Composable
fun ComparatorScreen(
    modifier: Modifier = Modifier,
    comparatorViewModel: ComparatorViewModel = koinViewModel(),
) {
    val tariffName by comparatorViewModel.tariffName.collectAsState()
    val powerTermRows by comparatorViewModel.powerTermRows.collectAsState()
    val energyConsumedRows by comparatorViewModel.energyConsumedRows.collectAsState()
    val iva by comparatorViewModel.iva.collectAsState()
    val impuestoElectrico by comparatorViewModel.impuestoElectrico.collectAsState()
    val isUploadingReport by comparatorViewModel.isUploadingReport.collectAsState()
    val proposalPriceList by comparatorViewModel.proposalPriceModelList.collectAsState()

    val context = LocalContext.current

    ComparatorView(
        modifier = modifier,
        tariffName = tariffName,
        powerTermRows = powerTermRows,
        energyConsumedRows = energyConsumedRows,
        iva = iva,
        impuestoElectrico = impuestoElectrico,
        isUploadingReport = isUploadingReport,
        proposalPriceList = proposalPriceList,
        onPdfSelected = { pdfFile ->
            comparatorViewModel.uploadConsumptionReport(pdfFile)
        },
        context = context
    )
}

/**
 * ComparatorView — A Compose-only UI that mirrors the provided mockup.
 * Focused on the View layer; can be fed with a UI model from upper layers later.
 */
@Composable
fun ComparatorView(
    modifier: Modifier = Modifier,
    tariffName: String,
    powerTermRows: List<Pair<String, Double>>,
    energyConsumedRows: List<Pair<String, Int>>,
    iva: String,
    impuestoElectrico: String,
    isUploadingReport: Boolean = false,
    onPdfSelected: (File) -> Unit = {},
    context: Context,
    proposalPriceList: List<ProposalPriceModel>,
) {
    Row(
        modifier = modifier
            .padding(end = 16.dp)
            .fillMaxSize()
            .statusBarsPadding()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        UserConsumptionDataView(
            modifier = modifier,
            tariffName = tariffName,
            powerTermRows = powerTermRows,
            energyConsumedRows = energyConsumedRows,
            iva = iva,
            impuestoElectrico = impuestoElectrico,
            isUploadingReport = isUploadingReport,
            onPdfSelected = onPdfSelected,
            context = context
        )

        for (proposal in proposalPriceList) {
            PriceProposalColumn(
                modifier = Modifier.padding(top = 75.dp),
                proposalTitle = proposal.proposalTitle,
                powerTermItems = proposal.powerTermItems,
                annualPowerTermCost = proposal.annualPowerTermCost,
                consumedEnergyItems = proposal.consumedEnergyItems,
                annualEnergyCost = proposal.annualEnergyCost,
                extraPricing = proposal.extraServices,
                electricTax = proposal.electricalTax,
                iva = proposal.iva,
                totalAnnualPrice = proposal.totalAnnualPrice,
            )
        }
    }
}

@Composable
fun UserConsumptionDataView(
    modifier: Modifier = Modifier,
    tariffName: String,
    powerTermRows: List<Pair<String, Double>>,
    energyConsumedRows: List<Pair<String, Int>>,
    iva: String,
    impuestoElectrico: String,
    isUploadingReport: Boolean = false,
    onPdfSelected: (File) -> Unit = {},
    context: Context,
) {
    Column(
        modifier
            .width(400.dp)
            .fillMaxHeight()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SelectFileButton(
            isUploadingReport = isUploadingReport,
            onPdfSelected = onPdfSelected,
            context = context
        )

        ComparatorTitleView(tariffName = tariffName)

        // Power term table
        SideTitleTableView(
            sideTitle = "TÉRMINO DE\nPOTENCIA",
            leftValues = powerTermRows.map { it.first },
            rightValues = powerTermRows.map { it.second.toString() },
        )

        SectionHeader(
            modifier = Modifier.fillMaxWidth(),
            text = "Coste anual termino potencia",
            background = HeaderYellow,
            corner = Corner
        )

        // Energy consumed table
        SideTitleTableView(
            sideTitle = "ENERGÍA\nCONSUMIDA",
            leftValues = energyConsumedRows.map { it.first },
            rightValues = energyConsumedRows.map { it.second.toString() },
        )

        SectionHeader(
            modifier = Modifier.fillMaxWidth(),
            text = "Coste anual termino energía",
            background = HeaderYellow,
            corner = Corner
        )

        // Extra services table (2 columns)
        SimpleTwoColumnTable(
            leftHeader = "SERVICIOS EXTRA",
            rightHeader = "Coste Anual",
            iva = iva,
            impuestoElectrico = impuestoElectrico,
        )

        // Final total banner
        HeaderBox(
            modifier = Modifier.fillMaxWidth(),
            text = "COSTE ANUAL FACTURA ELÉCTRICA",
            background = HeaderYellow,
            corner = Corner
        )
    }
}

@Composable
private fun ComparatorTitleView(
    tariffName: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HeaderBox(
            modifier = Modifier.weight(1.0f),
            text = tariffName,
            background = HeaderYellow,
            corner = Corner
        )
        HeaderBox(
            modifier = Modifier.weight(1.0f),
            text = "CONSUMO ANUAL",
            background = HeaderYellow,
            corner = Corner
        )
    }
}

@Composable
fun SelectFileButton(
    isUploadingReport: Boolean = false,
    onPdfSelected: (File) -> Unit = {},
    context: Context,
) {
    val fileButtonColors = ButtonDefaults.buttonColors(
        containerColor = AppPrimary,
        contentColor = AppOnPrimary,
        disabledContainerColor = AppPrimary.copy(alpha = 0.8f),
        disabledContentColor = AppOnPrimary
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
                color = AppOnPrimary
            )
        }
        Text(if (isUploadingReport) "Procesando..." else "Selecciona una factura")
    }
}

@Composable
private fun SimpleTwoColumnTable(
    leftHeader: String,
    rightHeader: String,
    iva: String,
    impuestoElectrico: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DynamicTableColumnView(
            modifier = Modifier.weight(1.0f),
            values = listOf(leftHeader, "IMPUESTO ELÉCTRICO", "IVA"),
        )
        DynamicTableColumnView(
            modifier = Modifier.weight(1.0f),
            values = listOf(rightHeader, impuestoElectrico, iva),
        )
    }
}

// Preview-only helpers and sample
data class ComparatorUiSample(
    val tariffName: String = "2.0TD",
    val powerRows: List<Pair<String, Double>> = listOf(
        "P1" to 5.50,
        "P2" to 5.50,
    ),
    val energyRows: List<Pair<String, Int>> = listOf(
        "P1" to 438,
        "P2" to 407,
        "P3" to 454,
    ),
    val extras: List<Pair<String, String>> = listOf(
        "IMPUESTO ELÉCTRICO" to "5.11%",
        "IVA" to "21%",
    )
)

@Preview(
    showBackground = true,
    device = PIXEL_TABLET
)
@Composable
private fun ComparatorViewPreview() {
    val context = LocalContext.current
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val sample = ComparatorUiSample()
            ComparatorView(
                tariffName = sample.tariffName,
                powerTermRows = sample.powerRows,
                energyConsumedRows = sample.energyRows,
                iva = "21",
                impuestoElectrico = "5.11",
                proposalPriceList = emptyList(),
                context = context,
            )
        }
    }
}
