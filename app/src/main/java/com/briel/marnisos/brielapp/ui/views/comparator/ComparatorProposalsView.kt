package com.briel.marnisos.brielapp.ui.views.comparator

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices.PIXEL_TABLET
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel
import com.briel.marnisos.brielapp.ui.models.ComparatorUiSample
import com.briel.marnisos.brielapp.ui.views.comparator.customerconditions.CustomerConditionsColumnView
import com.briel.marnisos.brielapp.ui.views.comparator.customerconditions.CustomerConditionsUiState
import com.briel.marnisos.brielapp.ui.views.pricetable.PriceProposalColumn

@Composable
internal fun ComparatorProposalsView(
    modifier: Modifier = Modifier,
    tariffName: String,
    powerTermRows: List<Pair<String, Double>>,
    energyConsumedRows: List<Pair<String, Int>>,
    iva: String,
    electricTax: String,
    visibleProposalPriceList: List<ProposalPriceModel>,
    proposalFixedAmountByTitle: Map<String, String>,
    onProposalFixedAmountChanged: (proposalTitle: String, fixedAmountInput: String) -> Unit,
    customerConditionsUiState: CustomerConditionsUiState,
) {
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Box(
        modifier = modifier
            .verticalScroll(verticalScrollState)
    ) {
        Row(
            modifier = Modifier
                .padding(end = 16.dp)
                .horizontalScroll(horizontalScrollState),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserConsumptionDataView(
                modifier = Modifier,
                tariffName = tariffName,
                powerTermRows = powerTermRows,
                energyConsumedRows = energyConsumedRows,
                iva = iva,
                impuestoElectrico = electricTax,
            )

            CustomerConditionsColumnView(
                modifier = Modifier,
                uiState = customerConditionsUiState,
            )

            for (proposal in visibleProposalPriceList) {
                PriceProposalColumn(
                    proposalTitle = proposal.proposalTitle,
                    powerTermItems = proposal.powerTermItems,
                    annualPowerTermCost = proposal.annualPowerTermCostFormatted,
                    consumedEnergyItems = proposal.consumedEnergyItems,
                    annualEnergyCost = proposal.annualEnergyCostFormatted,
                    electricTax = proposal.electricalTaxFormatted,
                    iva = proposal.ivaFormatted,
                    totalAnnualPrice = proposal.totalAnnualPriceFormatted,
                    fixedAmountInputValue = proposalFixedAmountByTitle[proposal.proposalTitle].orEmpty(),
                    onFixedAmountInputChange = { newValue ->
                        onProposalFixedAmountChanged(proposal.proposalTitle, newValue)
                    },
                )
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
private fun ComparatorProposalsPreview() {
    val sample = ComparatorUiSample()

    ComparatorProposalsView(
        modifier = Modifier,
        tariffName = sample.tariffName,
        powerTermRows = sample.powerRows,
        energyConsumedRows = sample.energyRows,
        iva = "21%",
        electricTax = "5.11%",
        visibleProposalPriceList = sample.proposals,
        proposalFixedAmountByTitle = emptyMap(),
        onProposalFixedAmountChanged = { _,_ -> },
        customerConditionsUiState = CustomerConditionsUiState(),
    )
}
