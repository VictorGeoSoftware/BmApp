package com.briel.marnisos.brielapp.ui.views.configuration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel

@Composable
internal fun ProposalVisibilityConfigurationView(
    modifier: Modifier = Modifier,
    proposalPriceList: List<ProposalPriceModel>,
    proposalVisibilityByTitle: Map<String, Boolean>,
    onProposalVisibilityChanged: (proposalTitle: String, isVisible: Boolean) -> Unit,
) {
    val verticalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(verticalScrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Propuestas visibles",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Usa el botón a la izquierda de la propuesta para ocultarla/mostrarla",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        HorizontalDivider()

        proposalPriceList.forEach { proposal ->
            val isVisible = proposalVisibilityByTitle[proposal.proposalTitle] ?: true
            ProposalVisibilityItem(
                proposalTitle = proposal.proposalTitle,
                isVisible = isVisible,
                onCheckedChange = { shouldBeVisible ->
                    onProposalVisibilityChanged(proposal.proposalTitle, shouldBeVisible)
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProposalVisibilityItem(
    proposalTitle: String,
    isVisible: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val alpha = if (isVisible) 1f else 0.45f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isVisible) }
            .alpha(alpha)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Switch(
            checked = isVisible,
            onCheckedChange = onCheckedChange,
        )

        Text(
            text = proposalTitle,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
    }
}
