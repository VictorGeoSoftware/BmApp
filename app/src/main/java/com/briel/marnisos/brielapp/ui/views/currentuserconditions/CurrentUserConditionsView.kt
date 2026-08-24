package com.briel.marnisos.brielapp.ui.views.currentuserconditions

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import com.briel.marnisos.brielapp.ui.components.CopyIcon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.briel.marnisos.brielapp.R
import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel
import com.briel.marnisos.brielapp.ui.theme.BrielAppTheme
import com.briel.marnisos.brielapp.ui.theme.Corner
import com.briel.marnisos.brielapp.ui.theme.extendedColors
import com.briel.marnisos.brielapp.ui.views.common.HeaderBox
import com.briel.marnisos.brielapp.ui.views.common.SectionHeader
import com.briel.marnisos.brielapp.ui.views.feefirst.FeeFirstGateBanner
import org.koin.androidx.compose.koinViewModel

/**
 * The fee-first gate. Until every power and energy period holds a valid price, the
 * proposals and configuration destinations stay locked.
 */
@Composable
internal fun CurrentUserConditionsScreen(
    onNavigateToProposals: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CurrentUserConditionsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isCopySheetVisible by rememberSaveable { mutableStateOf(false) }
    var selectedProposalTitle by rememberSaveable { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    if (isCopySheetVisible) {
        CopyProposalPricesSheet(
            proposals = uiState.availableProposals,
            selectedProposalTitle = selectedProposalTitle,
            onProposalSelected = { title -> selectedProposalTitle = title },
            onDismissRequest = { isCopySheetVisible = false },
            onConfirm = {
                val title = selectedProposalTitle ?: return@CopyProposalPricesSheet
                viewModel.copyPricesFromProposal(title)
                isCopySheetVisible = false
                Toast.makeText(
                    context,
                    context.getString(R.string.current_user_conditions_copy_success, title),
                    Toast.LENGTH_SHORT,
                ).show()
            },
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (uiState.hasFetchedConsumption) {
            FeeFirstGateBanner(
                gate = uiState.gate,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp),
            )
        }

        CurrentUserConditionsMainView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            hasFetchedProposalData = uiState.hasFetchedConsumption,
            uiState = uiState.form,
            supplyHolder = uiState.supplyHolder,
            supplyAddress = uiState.supplyAddress,
            supplyCupsCode = uiState.supplyCupsCode,
            onSupplyHolderChanged = viewModel::onSupplyHolderChanged,
            onSupplyAddressChanged = viewModel::onSupplyAddressChanged,
            onCompanyNameChanged = viewModel::onCompanyNameChanged,
            onCopyCurrentConditionsClicked = {
                if (!uiState.canCopyFromProposal) return@CurrentUserConditionsMainView
                selectedProposalTitle = selectedProposalTitle
                    ?.takeIf { selected ->
                        uiState.availableProposals.any { it.proposalTitle == selected }
                    }
                    ?: uiState.availableProposals.first().proposalTitle
                isCopySheetVisible = true
            },
            onPowerTermValueChanged = viewModel::onPowerTermValueChanged,
            onEnergyValueChanged = viewModel::onEnergyValueChanged,
            onExtraServicesChanged = viewModel::onExtraServicesChanged,
        )

        // Once every price is in, the screen is done but nothing told the broker where to
        // go next. The call to action only appears when the gate is actually open.
        if (uiState.gate.isUnlocked) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                onClick = {
                    viewModel.onNavigateToProposalsClicked()
                    onNavigateToProposals()
                },
            ) {
                Text(
                    text = stringResource(R.string.current_user_conditions_go_to_proposals),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CopyProposalPricesSheet(
    proposals: List<ProposalPriceModel>,
    selectedProposalTitle: String?,
    onProposalSelected: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        // The sheet ends with Cancel/Copy actions: a partially expanded sheet hides
        // them below the fold, so it must open fully expanded.
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
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
                    items = proposals,
                    key = { proposal -> proposal.proposalTitle },
                ) { proposal ->
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onProposalSelected(proposal.proposalTitle) },
                        headlineContent = { Text(text = proposal.proposalTitle) },
                        trailingContent = {
                            RadioButton(
                                selected = selectedProposalTitle == proposal.proposalTitle,
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


@Composable
private fun CurrentUserConditionsMainView(
    modifier: Modifier,
    hasFetchedProposalData: Boolean,
    uiState: CurrentUserConditionsFormState,
    supplyHolder: String,
    supplyAddress: String,
    supplyCupsCode: String,
    onSupplyHolderChanged: (String) -> Unit,
    onSupplyAddressChanged: (String) -> Unit,
    onCompanyNameChanged: (String) -> Unit,
    onCopyCurrentConditionsClicked: () -> Unit,
    onPowerTermValueChanged: (period: String, value: String) -> Unit,
    onEnergyValueChanged: (period: String, value: String) -> Unit,
    onExtraServicesChanged: (value: String) -> Unit,
) {
    val colors = extendedColors

    if (!hasFetchedProposalData) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.current_user_conditions_empty_state_message),
                color = colors.tableText,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HeaderBox(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.current_user_conditions_customer_data_title),
            background = colors.headerHighlight,
            corner = Corner,
        )

        EditableRowsSection(
            title = stringResource(R.string.current_user_conditions_customer_data_title),
            rows = emptyList(),
            onValueChanged = { _, _ -> },
            keyboardType = KeyboardType.Text,
            placeholderText = stringResource(R.string.current_user_conditions_customer_data_placeholder),
            headerStyle = EditableRowsSectionHeaderStyle.None,
            customContent = {
                CustomerDataInputRow(
                    label = stringResource(R.string.current_user_conditions_supply_holder_label),
                    value = supplyHolder,
                    onValueChanged = onSupplyHolderChanged,
                    placeholderText = stringResource(R.string.current_user_conditions_customer_data_placeholder),
                )

                CustomerDataInputRow(
                    label = stringResource(R.string.current_user_conditions_supply_address_label),
                    value = supplyAddress,
                    onValueChanged = onSupplyAddressChanged,
                    placeholderText = stringResource(R.string.current_user_conditions_customer_data_placeholder),
                )

                CustomerDataReadOnlyRow(
                    label = stringResource(R.string.current_user_conditions_cups_label),
                    value = supplyCupsCode,
                )

                // The incumbent supplier is not present in the bill read nor in the
                // proposals, so there is nothing to prefill it from: the broker types it.
                CustomerDataInputRow(
                    label = stringResource(R.string.current_user_conditions_company_name_label),
                    value = uiState.companyName,
                    onValueChanged = onCompanyNameChanged,
                    placeholderText = stringResource(
                        R.string.current_user_conditions_company_name_placeholder,
                    ),
                )
            }
        )

        CurrentConditionsHeader(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.current_user_conditions_title),
            onCopyClicked = onCopyCurrentConditionsClicked,
        )

        EditableRowsSection(
            title = stringResource(R.string.current_user_conditions_power_term_title),
            rows = uiState.powerTermRows,
            onValueChanged = onPowerTermValueChanged,
            headerStyle = EditableRowsSectionHeaderStyle.Subtle,
        )

        EditableRowsSection(
            title = stringResource(R.string.current_user_conditions_energy_consumed_title),
            rows = uiState.energyConsumedRows,
            onValueChanged = onEnergyValueChanged,
            headerStyle = EditableRowsSectionHeaderStyle.Subtle,
        )

        SectionHeader(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.current_user_conditions_extra_services_title),
            background = colors.headerHighlight,
            corner = Corner,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = colors.tableBorder),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(1f),
                text = stringResource(R.string.annual_amount),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.tableText,
            )

            OutlinedTextField(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .weight(2f),
                value = uiState.extraServices,
                onValueChange = onExtraServicesChanged,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                placeholder = { Text("0.00€") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colors.tableText,
                    unfocusedTextColor = colors.tableText,
                    focusedBorderColor = colors.tableBorder,
                    unfocusedBorderColor = colors.tableBorder,
                    cursorColor = colors.tableText,
                ),
            )
        }

        Spacer(Modifier.padding(bottom = 16.dp))
    }
}

@Composable
private fun CurrentConditionsHeader(
    modifier: Modifier = Modifier,
    title: String,
    onCopyClicked: () -> Unit,
) {
    val colors = extendedColors

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        HeaderBox(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            background = colors.headerHighlight,
            corner = Corner,
        )

        // Yellow is the section-label colour in this screen, so a yellow button read as a
        // second label. Dark fill + pill shape + icon make it unmistakably a control.
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            onClick = onCopyClicked,
            shape = RoundedCornerShape(percent = 50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
            ),
        ) {
            Icon(
                imageVector = CopyIcon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.current_user_conditions_copy_prices_action),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun EditableRowsSection(
    title: String,
    rows: List<Pair<String, String>>,
    onValueChanged: (period: String, value: String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Decimal,
    placeholderText: String = "0.00000000",
    headerStyle: EditableRowsSectionHeaderStyle = EditableRowsSectionHeaderStyle.Prominent,
    customContent: @Composable (() -> Unit)? = null,
) {
    val colors = extendedColors

    when (headerStyle) {
        EditableRowsSectionHeaderStyle.Prominent -> {
            SectionHeader(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                background = colors.headerHighlight,
                corner = Corner,
            )
        }

        EditableRowsSectionHeaderStyle.Subtle -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.headerBackground, RoundedCornerShape(Corner))
                    .border(1.dp, colors.sectionBorder, RoundedCornerShape(Corner))
                    .padding(vertical = 6.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                    color = colors.tableText,
                )
            }
        }

        EditableRowsSectionHeaderStyle.None -> Unit
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = colors.tableBorder),
    ) {
        customContent?.let {
            it()
            return@Column
        }

        for ((period, value) in rows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = period,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.tableText,
                )

                OutlinedTextField(
                    modifier = Modifier.weight(2f),
                    value = value,
                    onValueChange = { onValueChanged(period, it) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    placeholder = { Text(placeholderText) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.tableText,
                        unfocusedTextColor = colors.tableText,
                        focusedBorderColor = colors.tableBorder,
                        unfocusedBorderColor = colors.tableBorder,
                        cursorColor = colors.tableText,
                    ),
                )
            }
        }
    }
}

private enum class EditableRowsSectionHeaderStyle {
    Prominent,
    Subtle,
    None,
}

@Preview(name = "With Data - Light", showBackground = true)
@Composable
private fun CurrentUserConditionsPreviewWithData() {
    BrielAppTheme(darkTheme = false) {
        CurrentUserConditionsMainView(
            modifier = Modifier,
            hasFetchedProposalData = true,
            uiState = CurrentUserConditionsFormState(
                powerTermRows = listOf("P1" to "42.50", "P2" to "38.00"),
                energyConsumedRows = listOf("P1" to "0.12345678", "P2" to "0.09876543"),
                extraServices = "15.00",
            ),
            supplyHolder = "John Doe",
            supplyAddress = "123 Main Street",
            supplyCupsCode = "ES0031607515707001RC",
            onSupplyHolderChanged = {},
            onSupplyAddressChanged = {},
            onCompanyNameChanged = {},
            onCopyCurrentConditionsClicked = {},
            onPowerTermValueChanged = { _, _ -> },
            onEnergyValueChanged = { _, _ -> },
            onExtraServicesChanged = {},
        )
    }
}

@Preview(name = "Empty State - Light", showBackground = true)
@Composable
private fun CurrentUserConditionsPreviewEmptyState() {
    BrielAppTheme(darkTheme = false) {
        CurrentUserConditionsMainView(
            modifier = Modifier,
            hasFetchedProposalData = false,
            uiState = CurrentUserConditionsFormState(),
            supplyHolder = "",
            supplyAddress = "",
            supplyCupsCode = "",
            onSupplyHolderChanged = {},
            onSupplyAddressChanged = {},
            onCompanyNameChanged = {},
            onCopyCurrentConditionsClicked = {},
            onPowerTermValueChanged = { _, _ -> },
            onEnergyValueChanged = { _, _ -> },
            onExtraServicesChanged = {},
        )
    }
}

@Composable
private fun CustomerDataReadOnlyRow(
    label: String,
    value: String,
) {
    val colors = extendedColors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colors.tableText,
        )

        Text(
            modifier = Modifier.weight(2f),
            text = value,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = colors.tableText,
        )
    }
}

@Composable
private fun CustomerDataInputRow(
    label: String,
    value: String,
    onValueChanged: (String) -> Unit,
    placeholderText: String,
) {
    val colors = extendedColors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colors.tableText,
        )

        OutlinedTextField(
            modifier = Modifier.weight(2f),
            value = value,
            onValueChange = onValueChanged,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            placeholder = { Text(placeholderText) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = colors.tableText,
                unfocusedTextColor = colors.tableText,
                focusedBorderColor = colors.tableBorder,
                unfocusedBorderColor = colors.tableBorder,
                cursorColor = colors.tableText,
            ),
        )
    }
}
