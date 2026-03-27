package com.briel.marnisos.brielapp.ui.views.currentuserconditions

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        uiState = uiState,
        onPowerTermValueChanged = currentUserConditionsViewModel::onPowerTermValueChanged,
        onEnergyValueChanged = currentUserConditionsViewModel::onEnergyValueChanged,
        onExtraServicesChanged = currentUserConditionsViewModel::onExtraServicesChanged,
    )
}

@Composable
private fun CurrentUserConditionsMainView(
    modifier: Modifier,
    uiState: CurrentUserConditionsFormState,
    onPowerTermValueChanged: (period: String, value: String) -> Unit,
    onEnergyValueChanged: (period: String, value: String) -> Unit,
    onExtraServicesChanged: (value: String) -> Unit,
) {
    val colors = extendedColors
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
            text = "Condiciones actuales",
            background = colors.headerHighlight,
            corner = Corner,
        )

        EditableRowsSection(
            title = "TÉRMINO DE POTENCIA",
            rows = uiState.powerTermRows,
            onValueChanged = onPowerTermValueChanged,
        )

        EditableRowsSection(
            title = "ENERGÍA CONSUMIDA",
            rows = uiState.energyConsumedRows,
            onValueChanged = onEnergyValueChanged,
        )

        SectionHeader(
            modifier = Modifier.fillMaxWidth(),
            text = "SERVICIOS EXTRA",
            background = colors.headerHighlight,
            corner = Corner,
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = colors.tableBorder),
            value = uiState.extraServices,
            onValueChange = onExtraServicesChanged,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            placeholder = { Text("0.00000000") },
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

@Composable
private fun EditableRowsSection(
    title: String,
    rows: List<Pair<String, String>>,
    onValueChanged: (period: String, value: String) -> Unit,
) {
    val colors = extendedColors

    SectionHeader(
        modifier = Modifier.fillMaxWidth(),
        text = title,
        background = colors.headerHighlight,
        corner = Corner,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = colors.tableBorder),
    ) {
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    placeholder = { Text("0.00000000") },
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
