package com.briel.marnisos.brielapp.ui.views.comparator.customerconditions

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.briel.marnisos.brielapp.ui.components.tables.DynamicTableColumnView
import com.briel.marnisos.brielapp.ui.components.tables.HorizontalSeparator
import com.briel.marnisos.brielapp.ui.theme.Corner
import com.briel.marnisos.brielapp.ui.theme.extendedColors
import com.briel.marnisos.brielapp.ui.views.common.HeaderBox
import com.briel.marnisos.brielapp.ui.views.common.SectionHeader

@Composable
fun CustomerConditionsColumnView(
    modifier: Modifier = Modifier,
    uiState: CustomerConditionsUiState,
) {
    CustomerConditionsColumnMainView(
        modifier = modifier,
        powerTermItems = uiState.powerTermItems,
        annualPowerTermCost = uiState.annualPowerTermCost,
        consumedEnergyItems = uiState.consumedEnergyItems,
        annualEnergyCost = uiState.annualEnergyCost,
        extraServices = uiState.extraServices,
        electricTax = uiState.electricTax,
        iva = uiState.iva,
        totalAnnualPrice = uiState.totalAnnualPrice
    )
}

@Composable
fun CustomerConditionsColumnMainView(
    modifier: Modifier = Modifier,
    powerTermItems: List<Double>,
    annualPowerTermCost: String,
    consumedEnergyItems: List<Double>,
    annualEnergyCost: String,
    extraServices: String,
    electricTax: String,
    iva: String,
    totalAnnualPrice: String,
) {
    Column(
        modifier = modifier.then(
            Modifier.width(200.dp),
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CustomerConditionsTitleView()

        DynamicTableColumnView(
            modifier = Modifier.fillMaxWidth(),
            values = powerTermItems.map { it.toString() },
        )

        TotalSectionPriceView(
            content = "$annualPowerTermCost€",
        )

        DynamicTableColumnView(
            modifier = Modifier.fillMaxWidth(),
            values = consumedEnergyItems.map { it.toString() },
        )

        TotalSectionPriceView(
            content = "$annualEnergyCost€"
        )

        TaxesColumnView(
            modifier = Modifier.fillMaxWidth(),
            extraServices = extraServices,
            electricTax = electricTax,
            iva = iva
        )

        HeaderBox(
            modifier = Modifier.fillMaxWidth(),
            text = "$totalAnnualPrice€",
            background = extendedColors.headerHighlight,
            corner = Corner
        )
    }
}

@Composable
fun TotalSectionPriceView(content: String) {
    val colors = extendedColors

    SectionHeader(
        modifier = Modifier.fillMaxWidth(),
        text = content,
        background = colors.headerHighlight,
        corner = Corner
    )
}

@Composable
private fun CustomerConditionsTitleView() {
    val colors = extendedColors

    HeaderBox(
        modifier = Modifier.fillMaxWidth(),
        text = "Tarifa actual",
        background = colors.headerHighlight,
        corner = Corner
    )
}

@Composable
fun TaxesColumnView(
    modifier: Modifier = Modifier,
    extraServices: String,
    electricTax: String,
    iva: String,
) {
    val colors = extendedColors

    Column(
        modifier = modifier.then(
            Modifier.border(
                width = 1.dp,
                color = colors.tableBorder
            )
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier.height(68.dp).padding(12.dp),
            text = extraServices,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colors.tableText,
            textAlign = TextAlign.Center,
        )

        HorizontalSeparator()

        Text(
            modifier = Modifier.padding(12.dp),
            text = electricTax,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colors.tableText,
            textAlign = TextAlign.Center
        )

        HorizontalSeparator()

        Text(
            modifier = Modifier.padding(12.dp),
            text = iva,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colors.tableText,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun CustomerConditionsColumnMainViewPreview() {
    CustomerConditionsColumnMainView(
        powerTermItems = listOf(1.0, 2.0, 3.0),
        annualPowerTermCost = "100.00",
        consumedEnergyItems = listOf(1.0, 2.0, 3.0),
        annualEnergyCost = "100.00",
        extraServices = "32.00",
        electricTax = "5.11",
        iva = "21.00",
        totalAnnualPrice = "100.00",
    )
}
