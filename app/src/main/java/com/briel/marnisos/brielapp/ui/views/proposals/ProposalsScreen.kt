package com.briel.marnisos.brielapp.ui.views.proposals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.briel.marnisos.brielapp.R
import com.briel.marnisos.brielapp.ui.components.CheckCircleIcon
import com.briel.marnisos.brielapp.ui.theme.BrielAppTheme
import com.briel.marnisos.brielapp.ui.theme.extendedColors
import com.briel.marnisos.brielapp.ui.views.comparator.ComparatorProposalsView
import org.koin.androidx.compose.koinViewModel

private val BestOptionBackground = Color(0xFFDCFCE7)
private val BestOptionContent = Color(0xFF166534)

/**
 * Proposals comparison. The table itself is unchanged from the previous
 * implementation; the "best option" summary is appended underneath it.
 */
@Composable
internal fun ProposalsScreen(
    modifier: Modifier = Modifier,
    viewModel: ProposalsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProposalsContent(
        uiState = uiState,
        onProposalFixedAmountChanged = viewModel::onProposalFixedAmountChanged,
        modifier = modifier,
    )
}

@Composable
private fun ProposalsContent(
    uiState: ProposalsUiState,
    onProposalFixedAmountChanged: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!uiState.hasProposals) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.proposals_empty_message),
                style = MaterialTheme.typography.bodyMedium,
                color = extendedColors.tableTextSecondary,
            )
        }
        return
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ComparatorProposalsView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            tariffName = uiState.tariffName,
            powerTermRows = uiState.powerTermRows,
            energyConsumedRows = uiState.energyConsumedRows,
            iva = uiState.ivaLabel,
            electricTax = uiState.electricTaxLabel,
            visibleProposalPriceList = uiState.visibleProposals,
            proposalAnnualPriceDeltaByTitle = uiState.annualPriceDeltaByTitle,
            proposalAnnualSavingsPercentageByTitle = uiState.annualSavingsPercentageByTitle,
            proposalFixedAmountByTitle = uiState.fixedAmountByTitle,
            onProposalFixedAmountChanged = onProposalFixedAmountChanged,
            customerConditionsUiState = uiState.customerConditions,
        )

        uiState.bestProposalTitle?.let { bestProposalTitle ->
            BestProposalCard(
                proposalTitle = bestProposalTitle,
                annualSaving = uiState.bestProposalAnnualSaving.orEmpty(),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun BestProposalCard(
    proposalTitle: String,
    annualSaving: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(BestOptionBackground)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = CheckCircleIcon,
            contentDescription = null,
            tint = BestOptionContent,
            modifier = Modifier.size(20.dp),
        )

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = stringResource(R.string.proposals_best_option_title, proposalTitle),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = BestOptionContent,
            )
            Text(
                text = stringResource(R.string.proposals_best_option_message, annualSaving),
                style = MaterialTheme.typography.bodySmall,
                color = BestOptionContent,
            )
        }
    }
}

@Preview(name = "Best option card", showBackground = true)
@Composable
private fun BestProposalCardPreview() {
    BrielAppTheme(darkTheme = false) {
        BestProposalCard(
            proposalTitle = "Iberdrola Plan Estable",
            annualSaving = "189.67",
            modifier = Modifier.padding(16.dp),
        )
    }
}
