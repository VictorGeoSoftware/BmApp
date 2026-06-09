package com.briel.marnisos.brielapp.ui.views

import android.content.Context
import android.widget.Toast
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices.PIXEL_TABLET
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.briel.marnisos.brielapp.BuildConfig
import com.briel.marnisos.brielapp.R
import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel
import com.briel.marnisos.brielapp.ui.models.ComparatorDestination
import com.briel.marnisos.brielapp.ui.models.ComparatorUiSample
import com.briel.marnisos.brielapp.ui.views.common.TopActionBar
import com.briel.marnisos.brielapp.ui.views.comparator.ComparatorProposalsView
import com.briel.marnisos.brielapp.ui.views.comparator.customerconditions.CustomerConditionsUiState
import com.briel.marnisos.brielapp.ui.views.configuration.ProposalVisibilityConfigurationView
import com.briel.marnisos.brielapp.ui.views.currentuserconditions.CurrentUserConditionsView
import com.briel.marnisos.brielapp.ui.views.drawer.DrawerContent
import com.briel.marnisos.brielapp.ui.views.pricetable.ComparatorViewModel
import com.briel.marnisos.brielapp.ui.views.pricetable.export.ComparatorPdfShareManager
import com.briel.marnisos.brielapp.ui.views.scanner.CupsScannerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
    onLogoutClicked: () -> Unit = {},
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
    val proposalAnnualPriceDeltaByTitle by comparatorViewModel.proposalAnnualPriceDeltaByTitle.collectAsState()
    val proposalAnnualSavingsPercentageByTitle by comparatorViewModel.proposalAnnualSavingsPercentageByTitle.collectAsState()
    val proposalFixedAmountByTitle by comparatorViewModel.proposalFixedAmountByTitle.collectAsState()
    val proposalVisibilityByTitle by comparatorViewModel.proposalVisibilityByTitle.collectAsState()
    val customerConditionsUiState by comparatorViewModel.customerConditionsUiState.collectAsState()
    val supplyHolder by comparatorViewModel.supplyHolder.collectAsState()
    val supplyAddress by comparatorViewModel.supplyAddress.collectAsState()

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
        proposalAnnualPriceDeltaByTitle = proposalAnnualPriceDeltaByTitle,
        proposalAnnualSavingsPercentageByTitle = proposalAnnualSavingsPercentageByTitle,
        proposalFixedAmountByTitle = proposalFixedAmountByTitle,
        proposalVisibilityByTitle = proposalVisibilityByTitle,
        customerConditionsUiState = customerConditionsUiState,
        supplyHolder = supplyHolder,
        supplyAddress = supplyAddress,
        onBeforeFetchPriceProposals = comparatorViewModel::resetCurrentUserConditionsAndProposals,
        onPdfSelected = { pdfFile ->
            comparatorViewModel.uploadConsumptionReport(pdfFile)
        },
        onCupsCodeSelected = { cupsCode ->
            comparatorViewModel.uploadConsumptionReportByCups(cupsCode)
        },
        onGeneratePdfClick = comparatorViewModel::exportVisibleProposalsAsPdf,
        onProposalFixedAmountChanged = comparatorViewModel::updateProposalFixedAmount,
        onProposalVisibilityChanged = comparatorViewModel::setProposalVisibility,
        onCopyProposalToCurrentConditions = comparatorViewModel::copyProposalToCurrentUserConditions,
        onSupplyHolderChanged = comparatorViewModel::updateSupplyHolder,
        onSupplyAddressChanged = comparatorViewModel::updateSupplyAddress,
        onLogoutClicked = onLogoutClicked,
        versionLabel = BuildConfig.DEPLOY_VERSION,
        context = context
    )
}

/**
 * ComparatorView — A Compose-only UI that mirrors the provided mockup.
 * Focused on the View layer; can be fed with a UI model from upper layers later.
 */
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
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
    onBeforeFetchPriceProposals: () -> Unit = {},
    onPdfSelected: (File) -> Unit = {},
    onCupsCodeSelected: (String) -> Unit = {},
    onGeneratePdfClick: () -> Unit = {},
    context: Context,
    proposalPriceList: List<ProposalPriceModel>,
    proposalAnnualPriceDeltaByTitle: Map<String, Double>,
    proposalAnnualSavingsPercentageByTitle: Map<String, Int>,
    proposalFixedAmountByTitle: Map<String, String>,
    proposalVisibilityByTitle: Map<String, Boolean>,
    customerConditionsUiState: CustomerConditionsUiState,
    supplyHolder: String = "",
    supplyAddress: String = "",
    onProposalFixedAmountChanged: (proposalTitle: String, fixedAmountInput: String) -> Unit = { _, _ -> },
    onProposalVisibilityChanged: (proposalTitle: String, isVisible: Boolean) -> Unit = { _, _ -> },
    onCopyProposalToCurrentConditions: (proposalTitle: String) -> Unit = {},
    onSupplyHolderChanged: (String) -> Unit = {},
    onSupplyAddressChanged: (String) -> Unit = {},
    onLogoutClicked: () -> Unit = {},
    versionLabel: String,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedDestination by rememberSaveable { mutableStateOf(ComparatorDestination.CURRENT_USER_CONDITIONS) }
    var isCopyProposalSheetVisible by rememberSaveable { mutableStateOf(false) }
    var selectedProposalTitleForCopy by rememberSaveable { mutableStateOf<String?>(null) }

    val visibleProposals = proposalPriceList.filter { proposal ->
        proposalVisibilityByTitle[proposal.proposalTitle] ?: true
    }
    val hasFetchedProposalData = proposalPriceList.isNotEmpty()

    if (isCopyProposalSheetVisible) {
        CopyProposalPricesBottomSheet(
            visibleProposals = visibleProposals,
            selectedProposalTitle = selectedProposalTitleForCopy,
            onProposalSelected = { selectedProposalTitleForCopy = it },
            onDismissRequest = { isCopyProposalSheetVisible = false },
            onConfirm = {
                val selectedProposalTitle = selectedProposalTitleForCopy ?: return@CopyProposalPricesBottomSheet
                onCopyProposalToCurrentConditions(selectedProposalTitle)
                isCopyProposalSheetVisible = false
                Toast.makeText(
                    context,
                    context.getString(R.string.current_user_conditions_copy_success, selectedProposalTitle),
                    Toast.LENGTH_SHORT,
                ).show()
            },
        )
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
                    onLogoutClicked = {
                        scope.launch { drawerState.close() }
                        onLogoutClicked()
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
                onPdfSelected = { file ->
                    onBeforeFetchPriceProposals()
                    onPdfSelected(file)
                },
                onScanCupsSelected = {
                    selectedDestination = ComparatorDestination.CUPS_SCANNER
                },
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
                        proposalAnnualPriceDeltaByTitle = proposalAnnualPriceDeltaByTitle,
                        proposalAnnualSavingsPercentageByTitle = proposalAnnualSavingsPercentageByTitle,
                        proposalFixedAmountByTitle = proposalFixedAmountByTitle,
                        onProposalFixedAmountChanged = onProposalFixedAmountChanged,
                        customerConditionsUiState = customerConditionsUiState,
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

                ComparatorDestination.CURRENT_USER_CONDITIONS -> {
                    CurrentUserConditionsView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        powerPeriods = powerTermRows.map { item -> item.first },
                        energyPeriods = energyConsumedRows.map { item -> item.first },
                        hasFetchedProposalData = hasFetchedProposalData,
                        supplyHolder = supplyHolder,
                        supplyAddress = supplyAddress,
                        onSupplyHolderChanged = onSupplyHolderChanged,
                        onSupplyAddressChanged = onSupplyAddressChanged,
                        onCopyCurrentConditionsClicked = {
                            if (visibleProposals.isNotEmpty()) {
                                selectedProposalTitleForCopy = selectedProposalTitleForCopy
                                    ?.takeIf { selected ->
                                        visibleProposals.any { proposal -> proposal.proposalTitle == selected }
                                    }
                                    ?: visibleProposals.first().proposalTitle
                                isCopyProposalSheetVisible = true
                            }
                        },
                    )
                }

                ComparatorDestination.CUPS_SCANNER -> {
                    CupsScannerView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        isUploadingReport = isUploadingReport,
                        onBack = {
                            selectedDestination = ComparatorDestination.CURRENT_USER_CONDITIONS
                        },
                        onCupsConfirmed = { cupsCode ->
                            onBeforeFetchPriceProposals()
                            onCupsCodeSelected(cupsCode)
                            selectedDestination = ComparatorDestination.PROPOSALS
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CopyProposalPricesBottomSheet(
    visibleProposals: List<ProposalPriceModel>,
    selectedProposalTitle: String?,
    onProposalSelected: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        ) {
            Text(
                text = stringResource(R.string.current_user_conditions_copy_sheet_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            Text(
                text = stringResource(R.string.current_user_conditions_copy_sheet_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
            )

            Text(
                text = stringResource(R.string.current_user_conditions_copy_sheet_list_label),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp),
            ) {
                items(
                    items = visibleProposals,
                    key = { proposal -> proposal.proposalTitle },
                ) { proposal ->
                    val isSelected = selectedProposalTitle == proposal.proposalTitle
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onProposalSelected(proposal.proposalTitle) },
                        headlineContent = {
                            Text(text = proposal.proposalTitle)
                        },
                        trailingContent = {
                            RadioButton(
                                selected = isSelected,
                                onClick = null,
                            )
                        },
                    )
                }
            }

            Text(
                text = stringResource(R.string.current_user_conditions_copy_sheet_helper),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDismissRequest,
                ) {
                    Text(text = stringResource(R.string.cancel))
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onConfirm,
                    enabled = selectedProposalTitle != null,
                ) {
                    Text(text = stringResource(R.string.current_user_conditions_copy_button))
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
                proposalAnnualPriceDeltaByTitle = emptyMap(),
                proposalAnnualSavingsPercentageByTitle = emptyMap(),
                proposalFixedAmountByTitle = emptyMap(),
                proposalVisibilityByTitle = emptyMap(),
                customerConditionsUiState = CustomerConditionsUiState(),
                versionLabel = "v2603_1316",
                context = context,
            )
        }
    }
}
