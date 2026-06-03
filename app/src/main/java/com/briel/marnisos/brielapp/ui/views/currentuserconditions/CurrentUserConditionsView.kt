package com.briel.marnisos.brielapp.ui.views.currentuserconditions

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.briel.marnisos.brielapp.R
import com.briel.marnisos.brielapp.ui.theme.BrielAppTheme
import com.briel.marnisos.brielapp.ui.theme.Corner
import com.briel.marnisos.brielapp.ui.theme.extendedColors
import com.briel.marnisos.brielapp.ui.views.common.HeaderBox
import com.briel.marnisos.brielapp.ui.views.common.SectionHeader
import org.koin.androidx.compose.koinViewModel

@Composable
fun CurrentUserConditionsView(
    modifier: Modifier = Modifier,
    powerPeriods: List<String>,
    energyPeriods: List<String>,
    hasFetchedProposalData: Boolean,
    supplyHolder: String,
    supplyAddress: String,
    onSupplyHolderChanged: (String) -> Unit,
    onSupplyAddressChanged: (String) -> Unit,
    onCopyCurrentConditionsClicked: () -> Unit = {},
    currentUserConditionsViewModel: CurrentUserConditionsViewModel = koinViewModel(),
) {
    val uiState by currentUserConditionsViewModel.uiState.collectAsState()

    LaunchedEffect(powerPeriods, energyPeriods) {
        currentUserConditionsViewModel.ensurePeriods(
            powerPeriods = powerPeriods,
            energyPeriods = energyPeriods,
        )
    }

    CurrentUserConditionsMainView(
        modifier = modifier,
        hasFetchedProposalData = hasFetchedProposalData,
        uiState = uiState,
        supplyHolder = supplyHolder,
        supplyAddress = supplyAddress,
        onSupplyHolderChanged = onSupplyHolderChanged,
        onSupplyAddressChanged = onSupplyAddressChanged,
        onCopyCurrentConditionsClicked = onCopyCurrentConditionsClicked,
        onPowerTermValueChanged = currentUserConditionsViewModel::onPowerTermValueChanged,
        onEnergyValueChanged = currentUserConditionsViewModel::onEnergyValueChanged,
        onExtraServicesChanged = currentUserConditionsViewModel::onExtraServicesChanged,
    )
}

@Composable
private fun CurrentUserConditionsMainView(
    modifier: Modifier,
    hasFetchedProposalData: Boolean,
    uiState: CurrentUserConditionsFormState,
    supplyHolder: String,
    supplyAddress: String,
    onSupplyHolderChanged: (String) -> Unit,
    onSupplyAddressChanged: (String) -> Unit,
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

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HeaderBox(
            modifier = Modifier.weight(1f),
            text = title,
            background = colors.headerHighlight,
            corner = Corner,
        )

        TextButton(
            modifier = Modifier
                .border(width = 1.dp, color = colors.sectionBorder, shape = RoundedCornerShape(Corner))
                .background(colors.headerBackground, RoundedCornerShape(Corner)),
            onClick = onCopyClicked,
        ) {
            Text(
                text = stringResource(R.string.current_user_conditions_copy_button),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = colors.tableText,
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
            onSupplyHolderChanged = {},
            onSupplyAddressChanged = {},
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
            onSupplyHolderChanged = {},
            onSupplyAddressChanged = {},
            onCopyCurrentConditionsClicked = {},
            onPowerTermValueChanged = { _, _ -> },
            onEnergyValueChanged = { _, _ -> },
            onExtraServicesChanged = {},
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
