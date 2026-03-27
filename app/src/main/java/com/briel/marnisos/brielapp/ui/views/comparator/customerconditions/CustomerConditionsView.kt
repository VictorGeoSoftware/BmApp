package com.briel.marnisos.brielapp.ui.views.comparator.customerconditions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.briel.marnisos.brielapp.ui.components.tables.DynamicTableColumnView
import com.briel.marnisos.brielapp.ui.theme.Corner
import com.briel.marnisos.brielapp.ui.theme.extendedColors
import com.briel.marnisos.brielapp.ui.views.common.HeaderBox
import com.briel.marnisos.brielapp.ui.views.common.SectionHeader
import org.koin.androidx.compose.koinViewModel

@Composable
fun CustomerConditionsView(
    modifier: Modifier = Modifier,
    customerConditionsViewModel: CustomerConditionsViewModel = koinViewModel(),
) {
    val powerTermItems by customerConditionsViewModel.powerTermItems.collectAsState()
    val annualPowerTermCost by customerConditionsViewModel.annualPowerTermCost.collectAsState()
    val consumedEnergyItems by customerConditionsViewModel.consumedEnergyItems.collectAsState()
    val annualEnergyCost by customerConditionsViewModel.annualEnergyCost.collectAsState()
    val extraServices by customerConditionsViewModel.extraServices.collectAsState()
    val electricTax by customerConditionsViewModel.electricTax.collectAsState()
    val iva by customerConditionsViewModel.iva.collectAsState()
    val totalAnnualPrice by customerConditionsViewModel.totalAnnualPrice.collectAsState()

    CustomerConditionsMainView(
        modifier = modifier,
        powerTermItems = powerTermItems,
        annualPowerTermCost = annualPowerTermCost,
        consumedEnergyItems = consumedEnergyItems,
        annualEnergyCost = annualEnergyCost,
        extraServices = extraServices,
        electricTax = electricTax,
        iva = iva,
        totalAnnualPrice = totalAnnualPrice
    )
}

@Composable
fun CustomerConditionsMainView(
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
        CustomerConditionsTitleView(
        )

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

        DynamicTableColumnView(
            modifier = Modifier.fillMaxWidth(),
            values = listOf(extraServices, electricTax, iva),
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

    SectionHeader(
        modifier = Modifier.fillMaxWidth(),
        text = "Tarifa actual",
        background = colors.headerHighlight,
        corner = Corner
    )
}

@Composable
@Preview(showBackground = true)
private fun CustomerConditionsMainViewPreview() {
    CustomerConditionsMainView(
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
