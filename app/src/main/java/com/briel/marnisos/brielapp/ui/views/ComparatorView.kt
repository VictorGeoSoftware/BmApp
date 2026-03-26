package com.briel.marnisos.brielapp.ui.views

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices.PIXEL_TABLET
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.widget.Toast
import com.briel.marnisos.brielapp.BuildConfig
import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel
import com.briel.marnisos.brielapp.ui.Utils.uriToFile
import com.briel.marnisos.brielapp.ui.components.tables.DynamicTableColumnView
import com.briel.marnisos.brielapp.ui.components.tables.SideTitleTableView
import com.briel.marnisos.brielapp.ui.theme.Corner
import com.briel.marnisos.brielapp.ui.theme.extendedColors
import com.briel.marnisos.brielapp.ui.views.common.HeaderBox
import com.briel.marnisos.brielapp.ui.views.common.SectionHeader
import com.briel.marnisos.brielapp.ui.views.pricetable.ComparatorViewModel
import com.briel.marnisos.brielapp.ui.views.pricetable.PriceProposalColumn
import com.briel.marnisos.brielapp.ui.views.pricetable.export.ComparatorPdfShareManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
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
    val isGeneratingPdf by comparatorViewModel.isGeneratingPdf.collectAsState()
    val pdfExportError by comparatorViewModel.pdfExportError.collectAsState()
    val proposalPriceList by comparatorViewModel.proposalPriceModelList.collectAsState()
    val proposalFixedAmountByTitle by comparatorViewModel.proposalFixedAmountByTitle.collectAsState()
    val proposalVisibilityByTitle by comparatorViewModel.proposalVisibilityByTitle.collectAsState()

    val context = LocalContext.current
    val pdfShareManager = remember { ComparatorPdfShareManager() }

    LaunchedEffect(comparatorViewModel, context, pdfShareManager) {
        comparatorViewModel.generatedPdfFile.collectLatest { generatedFile ->
            pdfShareManager.sharePdf(context, generatedFile)
        }
    }

    LaunchedEffect(pdfExportError, context) {
        pdfExportError?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            comparatorViewModel.clearPdfExportError()
        }
    }

    ComparatorView(
        modifier = modifier,
        tariffName = tariffName,
        powerTermRows = powerTermRows,
        energyConsumedRows = energyConsumedRows,
        iva = iva,
        impuestoElectrico = impuestoElectrico,
        isUploadingReport = isUploadingReport,
        isGeneratingPdf = isGeneratingPdf,
        proposalPriceList = proposalPriceList,
        proposalFixedAmountByTitle = proposalFixedAmountByTitle,
        proposalVisibilityByTitle = proposalVisibilityByTitle,
        onPdfSelected = { pdfFile ->
            comparatorViewModel.uploadConsumptionReport(pdfFile)
        },
        onGeneratePdfClick = comparatorViewModel::exportVisibleProposalsAsPdf,
        onProposalFixedAmountChanged = comparatorViewModel::updateProposalFixedAmount,
        onProposalVisibilityChanged = comparatorViewModel::setProposalVisibility,
        versionLabel = BuildConfig.DEPLOY_VERSION,
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
    isGeneratingPdf: Boolean = false,
    onPdfSelected: (File) -> Unit = {},
    onGeneratePdfClick: () -> Unit = {},
    context: Context,
    proposalPriceList: List<ProposalPriceModel>,
    proposalFixedAmountByTitle: Map<String, String>,
    proposalVisibilityByTitle: Map<String, Boolean>,
    onProposalFixedAmountChanged: (proposalTitle: String, fixedAmountInput: String) -> Unit = { _, _ -> },
    onProposalVisibilityChanged: (proposalTitle: String, isVisible: Boolean) -> Unit = { _, _ -> },
    versionLabel: String,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedDestination by rememberSaveable { mutableStateOf(ComparatorDestination.PROPOSALS) }

    val visibleProposals = proposalPriceList.filter { proposal ->
        proposalVisibilityByTitle[proposal.proposalTitle] ?: true
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.fillMaxHeight()) {
                DrawerContent(
                    selectedDestination = selectedDestination,
                    onDestinationSelected = { destination ->
                        selectedDestination = destination
                        scope.launch { drawerState.close() }
                    },
                    versionLabel = versionLabel,
                )
            }
        },
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            TopActionBar(
                isUploadingReport = isUploadingReport,
                isGeneratingPdf = isGeneratingPdf,
                showPrintButton = selectedDestination == ComparatorDestination.PROPOSALS && visibleProposals.isNotEmpty(),
                onGeneratePdfClick = onGeneratePdfClick,
                onPdfSelected = onPdfSelected,
                context = context,
                onOpenDrawer = {
                    scope.launch { drawerState.open() }
                }
            )

            when (selectedDestination) {
                ComparatorDestination.PROPOSALS -> {
                    ComparatorProposalsView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        tariffName = tariffName,
                        powerTermRows = powerTermRows,
                        energyConsumedRows = energyConsumedRows,
                        iva = iva,
                        impuestoElectrico = impuestoElectrico,
                        visibleProposalPriceList = visibleProposals,
                        proposalFixedAmountByTitle = proposalFixedAmountByTitle,
                        onProposalFixedAmountChanged = onProposalFixedAmountChanged,
                    )
                }

                ComparatorDestination.CONFIGURATION -> {
                    ProposalVisibilityConfigurationView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        proposalPriceList = proposalPriceList,
                        proposalVisibilityByTitle = proposalVisibilityByTitle,
                        onProposalVisibilityChanged = onProposalVisibilityChanged,
                    )
                }
            }
        }
    }
}

private enum class ComparatorDestination {
    PROPOSALS,
    CONFIGURATION,
}

@Composable
private fun DrawerContent(
    selectedDestination: ComparatorDestination,
    onDestinationSelected: (ComparatorDestination) -> Unit,
    versionLabel: String,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .padding(horizontal = 12.dp, vertical = 24.dp)
    ) {
        Text(
            text = "Menú",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )

        NavigationDrawerItem(
            label = { Text("Propuestas") },
            selected = selectedDestination == ComparatorDestination.PROPOSALS,
            onClick = { onDestinationSelected(ComparatorDestination.PROPOSALS) }
        )

        NavigationDrawerItem(
            label = { Text("Configuración") },
            selected = selectedDestination == ComparatorDestination.CONFIGURATION,
            onClick = { onDestinationSelected(ComparatorDestination.CONFIGURATION) }
        )

        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = versionLabel,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.End)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun TopActionBar(
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

@Composable
private fun PrintButton(
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

@Composable
private fun ComparatorProposalsView(
    modifier: Modifier = Modifier,
    tariffName: String,
    powerTermRows: List<Pair<String, Double>>,
    energyConsumedRows: List<Pair<String, Int>>,
    iva: String,
    impuestoElectrico: String,
    visibleProposalPriceList: List<ProposalPriceModel>,
    proposalFixedAmountByTitle: Map<String, String>,
    onProposalFixedAmountChanged: (proposalTitle: String, fixedAmountInput: String) -> Unit,
) {
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Box(
        modifier = modifier
            .verticalScroll(verticalScrollState)
    ) {
        Row(
            modifier = Modifier
                .padding(end = 16.dp)
                .horizontalScroll(horizontalScrollState),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserConsumptionDataView(
                modifier = Modifier,
                tariffName = tariffName,
                powerTermRows = powerTermRows,
                energyConsumedRows = energyConsumedRows,
                iva = iva,
                impuestoElectrico = impuestoElectrico,
            )

            for (proposal in visibleProposalPriceList) {
                PriceProposalColumn(
                    proposalTitle = proposal.proposalTitle,
                    powerTermItems = proposal.powerTermItems,
                    annualPowerTermCost = proposal.annualPowerTermCostFormatted,
                    consumedEnergyItems = proposal.consumedEnergyItems,
                    annualEnergyCost = proposal.annualEnergyCostFormatted,
                    electricTax = proposal.electricalTaxFormatted,
                    iva = proposal.ivaFormatted,
                    totalAnnualPrice = proposal.totalAnnualPriceFormatted,
                    fixedAmountInputValue = proposalFixedAmountByTitle[proposal.proposalTitle].orEmpty(),
                    onFixedAmountInputChange = { newValue ->
                        onProposalFixedAmountChanged(proposal.proposalTitle, newValue)
                    },
                )
            }
        }
    }
}

@Composable
private fun ProposalVisibilityConfigurationView(
    modifier: Modifier = Modifier,
    proposalPriceList: List<ProposalPriceModel>,
    proposalVisibilityByTitle: Map<String, Boolean>,
    onProposalVisibilityChanged: (proposalTitle: String, isVisible: Boolean) -> Unit,
) {
    val verticalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(verticalScrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Propuestas visibles",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Usa el botón a la izquierda de la propuesta para ocultarla/mostrarla",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        HorizontalDivider()

        proposalPriceList.forEach { proposal ->
            val isVisible = proposalVisibilityByTitle[proposal.proposalTitle] ?: true
            ProposalVisibilityItem(
                proposalTitle = proposal.proposalTitle,
                isVisible = isVisible,
                onCheckedChange = { shouldBeVisible ->
                    onProposalVisibilityChanged(proposal.proposalTitle, shouldBeVisible)
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProposalVisibilityItem(
    proposalTitle: String,
    isVisible: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val alpha = if (isVisible) 1f else 0.45f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isVisible) }
            .alpha(alpha)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Switch(
            checked = isVisible,
            onCheckedChange = onCheckedChange,
        )

        Text(
            text = proposalTitle,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
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
) {
    val colors = extendedColors

    Column(
        modifier
            .width(400.dp)
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
            background = colors.headerHighlight,
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
            background = colors.headerHighlight,
            corner = Corner
        )

        // Extra services table (2 columns)
        SimpleTwoColumnHeader(
            modifier = Modifier.height(57.dp),
            leftHeader = "SERVICIOS EXTRA",
            rightHeader = "Coste Anual",
        )
        SimpleTwoColumnTable(
            iva = iva,
            impuestoElectrico = impuestoElectrico,
        )

        // Final total banner
        HeaderBox(
            modifier = Modifier.fillMaxWidth(),
            text = "COSTE ANUAL FACTURA ELÉCTRICA",
            background = colors.headerHighlight,
            corner = Corner
        )
    }
}

@Composable
private fun ComparatorTitleView(
    tariffName: String
) {
    val colors = extendedColors

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HeaderBox(
            modifier = Modifier.weight(1.0f),
            text = tariffName,
            background = colors.headerHighlight,
            corner = Corner
        )
        HeaderBox(
            modifier = Modifier.weight(1.0f),
            text = "CONSUMO ANUAL",
            background = colors.headerHighlight,
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

@Composable
private fun SimpleTwoColumnHeader(
    modifier: Modifier = Modifier,
    leftHeader: String,
    rightHeader: String,
) {
    Row(
        modifier = modifier.then(Modifier.fillMaxWidth()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DynamicTableColumnView(
            modifier = Modifier.weight(1.0f),
            values = listOf(leftHeader),
        )
        DynamicTableColumnView(
            modifier = Modifier.weight(1.0f),
            values = listOf(rightHeader),
        )
    }
}

@Composable
private fun SimpleTwoColumnTable(
    iva: String,
    impuestoElectrico: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DynamicTableColumnView(
            modifier = Modifier.weight(1.0f),
            values = listOf("IMPUESTO ELÉCTRICO", "IVA"),
        )
        DynamicTableColumnView(
            modifier = Modifier.weight(1.0f),
            values = listOf(impuestoElectrico, iva),
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
                proposalFixedAmountByTitle = emptyMap(),
                proposalVisibilityByTitle = emptyMap(),
                versionLabel = "v2603_1316",
                context = context,
            )
        }
    }
}
