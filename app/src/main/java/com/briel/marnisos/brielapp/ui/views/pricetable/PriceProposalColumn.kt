package com.briel.marnisos.brielapp.ui.views.pricetable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.briel.marnisos.brielapp.ui.components.tables.DynamicTableColumnView
import com.briel.marnisos.brielapp.ui.components.tables.ExtraServicesAndTaxesRow
import com.briel.marnisos.brielapp.ui.theme.Corner
import com.briel.marnisos.brielapp.ui.theme.extendedColors
import com.briel.marnisos.brielapp.ui.views.common.HeaderBox
import com.briel.marnisos.brielapp.ui.views.common.SectionHeader
import java.util.Locale

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
    annualPriceDifference: Double?,
    annualSavingsPercentage: Int?,
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
            values = powerTermItems.map { it.toSixDecimalsString() },
        )

        TotalSectionPriceView(
            content = "$annualPowerTermCost€",
        )

        DynamicTableColumnView(
            modifier = Modifier.fillMaxWidth(),
            values = consumedEnergyItems.map { it.toSixDecimalsString() },
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

        AnnualPriceDifferenceSection(
            modifier = Modifier.fillMaxWidth(),
            annualPriceDifference = annualPriceDifference,
            annualSavingsPercentage = annualSavingsPercentage,
        )
    }
}

@Composable
private fun AnnualPriceDifferenceSection(
    annualPriceDifference: Double?,
    annualSavingsPercentage: Int?,
    modifier: Modifier = Modifier,
) {
    val colors = extendedColors
    val textColor = when {
        annualPriceDifference == null -> colors.tableText
        annualPriceDifference < 0.0 -> Color(0xFFD32F2F)
        annualPriceDifference > 0.0 -> Color(0xFF000000)
        else -> colors.tableText
    }

    val sectionText = annualPriceDifference?.let { value ->
        val annualDifferenceText = String.format(java.util.Locale.US, "%.2f€", value)
        val annualSavingsText = annualSavingsPercentage?.let { " (${it}%)" }.orEmpty()
        annualDifferenceText + annualSavingsText
    } ?: "--"

    Row(
        modifier = modifier
            .background(colors.sectionHighlight, RoundedCornerShape(Corner))
            .border(1.dp, colors.sectionBorder, RoundedCornerShape(Corner))
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = sectionText,
            color = textColor,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
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

private fun Double.toSixDecimalsString(): String = String.format(Locale.US, "%.6f", this)

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
        annualPriceDifference = -29.51,
        annualSavingsPercentage = -10,
        fixedAmountInputValue = "",
        onFixedAmountInputChange = {},
    )
}
