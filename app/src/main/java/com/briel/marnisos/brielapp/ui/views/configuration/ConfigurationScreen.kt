package com.briel.marnisos.brielapp.ui.views.configuration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

/**
 * Stateful entry point for the proposal-visibility configuration destination.
 *
 * The list UI itself is unchanged; only its state source moved to a screen ViewModel.
 */
@Composable
internal fun ConfigurationScreen(
    modifier: Modifier = Modifier,
    viewModel: ConfigurationViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProposalVisibilityConfigurationView(
        modifier = modifier,
        proposalPriceList = uiState.proposals,
        proposalVisibilityByTitle = uiState.visibilityByTitle,
        onProposalVisibilityChanged = viewModel::onProposalVisibilityChanged,
    )
}
