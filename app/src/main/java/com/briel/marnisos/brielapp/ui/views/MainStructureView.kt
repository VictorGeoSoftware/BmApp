package com.briel.marnisos.brielapp.ui.views

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices.PIXEL_TABLET
import androidx.compose.ui.tooling.preview.Preview
import android.widget.Toast
import com.briel.marnisos.brielapp.BuildConfig
import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel
import com.briel.marnisos.brielapp.ui.models.ComparatorDestination
import com.briel.marnisos.brielapp.ui.models.ComparatorUiSample
import com.briel.marnisos.brielapp.ui.views.common.TopActionBar
import com.briel.marnisos.brielapp.ui.views.comparator.ComparatorProposalsView
import com.briel.marnisos.brielapp.ui.views.configuration.ProposalVisibilityConfigurationView
import com.briel.marnisos.brielapp.ui.views.drawer.DrawerContent
import com.briel.marnisos.brielapp.ui.views.pricetable.ComparatorViewModel
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
fun MainView(
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

    MainStructureView(
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
fun MainStructureView(
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
                        electricTax = impuestoElectrico,
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

// Preview-only helpers and sample
@Preview(
    showBackground = true,
    device = PIXEL_TABLET
)
@Composable
private fun MainStructureViewPreview() {
    val context = LocalContext.current
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val sample = ComparatorUiSample()
            MainStructureView(
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
