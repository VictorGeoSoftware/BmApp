package com.briel.marnisos.brielapp.ui.views.pricetable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.briel.marnisos.brielapp.ui.components.tables.DynamicTableColumnView
import com.briel.marnisos.brielapp.ui.components.tables.ExtraServicesAndTaxesRow
import com.briel.marnisos.brielapp.ui.theme.Corner
import com.briel.marnisos.brielapp.ui.theme.extendedColors
import com.briel.marnisos.brielapp.ui.views.common.HeaderBox
import com.briel.marnisos.brielapp.ui.views.common.SectionHeader

@Composable
fun PriceProposalColumn(
    modifier: Modifier = Modifier,
    proposalTitle: String,
    powerTermItems: List<Double>,
    annualPowerTermCost: String,
    consumedEnergyItems: List<Double>,
    annualEnergyCost: String,
    electricTax: String,
    iva: String,
    totalAnnualPrice: String,
    fixedAmountInputValue: String,
    onFixedAmountInputChange: (String) -> Unit,
) {
    val colors = extendedColors

    Column(
        modifier = modifier.then(
            Modifier.width(200.dp),
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProposalTitleView(
            content = proposalTitle
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

        ExtraServicesAndTaxesRow(
            modifier = Modifier.fillMaxWidth(),
            iva = iva,
            electricityTax = electricTax,
            fixedAmountInputValue = fixedAmountInputValue,
            onFixedAmountInputChange = onFixedAmountInputChange
        )

        HeaderBox(
            modifier = Modifier.fillMaxWidth(),
            text = "$totalAnnualPrice€",
            background = colors.headerHighlight,
            corner = Corner
        )

        SectionHeader(
            modifier = Modifier.fillMaxWidth(),
            text = "pending/ pending",
            background = colors.sectionHighlight,
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
private fun ProposalTitleView(content: String) {
    val colors = extendedColors

    HeaderBox(
        modifier = Modifier.fillMaxWidth(),
        text = content,
        background = colors.headerBackground,
        corner = Corner
    )
}

@Composable
@Preview(showBackground = true)
private fun PriceProposalColumnPreview() {
    PriceProposalColumn(
        proposalTitle = "Propuesta 1",
        powerTermItems = listOf(1.0, 2.0, 3.0),
        annualPowerTermCost = "100.00",
        consumedEnergyItems = listOf(1.0, 2.0, 3.0),
        annualEnergyCost = "100.00",
        electricTax = "2.00",
        iva = "2.00",
        totalAnnualPrice = "100.00",
        fixedAmountInputValue = "",
        onFixedAmountInputChange = {},
    )
}
