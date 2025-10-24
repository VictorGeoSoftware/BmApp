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
import com.briel.marnisos.brielapp.ui.theme.Corner
import com.briel.marnisos.brielapp.ui.theme.HeaderWhite
import com.briel.marnisos.brielapp.ui.theme.HeaderYellow
import com.briel.marnisos.brielapp.ui.theme.HighlightBlue
import com.briel.marnisos.brielapp.ui.views.common.HeaderBox
import com.briel.marnisos.brielapp.ui.views.common.SectionHeader

@Composable
fun PriceProposalColumn(
    modifier: Modifier = Modifier,
    proposalTitle: String,
    powerTermItems: List<String>,
    annualPowerTermCost: String,
    consumedEnergyItems: List<String>,
    annualEnergyCost: String,
    extraPricingItems: List<String>,
    totalAnnualPrice: String,
    savings: Pair<String, String>,
) {
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
            values = powerTermItems,
        )

        TotalSectionPriceView(
            content = annualPowerTermCost,
        )

        DynamicTableColumnView(
            modifier = Modifier.fillMaxWidth(),
            values = consumedEnergyItems,
        )

        TotalSectionPriceView(
            content = annualEnergyCost,
        )

        DynamicTableColumnView(
            modifier = Modifier.fillMaxWidth(),
            values = extraPricingItems,
        )

        TotalSectionPriceView(
            content = totalAnnualPrice,
        )

        SectionHeader(
            modifier = Modifier.fillMaxWidth(),
            text = "${savings.first} / ${savings.second}",
            background = HighlightBlue,
            corner = Corner
        )
    }
}

@Composable
fun TotalSectionPriceView(content: String) {
    SectionHeader(
        modifier = Modifier.fillMaxWidth(),
        text = content,
        background = HeaderYellow,
        corner = Corner
    )
}

@Composable
private fun ProposalTitleView(content: String) {
    HeaderBox(
        modifier = Modifier.fillMaxWidth(),
        text = content,
        background = HeaderWhite,
        corner = Corner
    )
}

@Composable
@Preview(showBackground = true)
private fun PriceProposalColumnPreview() {
    PriceProposalColumn(
        proposalTitle = "Propuesta 1",
        powerTermItems = listOf("1", "2", "3"),
        annualPowerTermCost = "100",
        consumedEnergyItems = listOf("1", "2", "3"),
        annualEnergyCost = "100",
        extraPricingItems = listOf("1", "2", "3"),
        totalAnnualPrice = "100",
        savings = Pair("100", "100"),
    )
}
