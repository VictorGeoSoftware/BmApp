package com.briel.marnisos.brielapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.briel.marnisos.brielapp.domain.models.FeeFirstStage
import com.briel.marnisos.brielapp.ui.views.configuration.ConfigurationScreen
import com.briel.marnisos.brielapp.ui.views.currentuserconditions.CurrentUserConditionsScreen
import com.briel.marnisos.brielapp.ui.views.fetchconsumption.FetchConsumptionScreen
import com.briel.marnisos.brielapp.ui.views.fetchconsumption.FetchConsumptionViewModel
import com.briel.marnisos.brielapp.ui.views.proposals.ProposalsScreen
import com.briel.marnisos.brielapp.ui.views.scanner.CupsScannerView
import org.koin.androidx.compose.koinViewModel

/**
 * Single navigation graph of the authenticated area.
 *
 * The fee-first gate is enforced here rather than in each screen: whenever [stage]
 * no longer satisfies the current destination's requirement, the user is redirected
 * to the appropriate fallback (rule R5). This also covers state restoration and any
 * future deep links.
 */
@Composable
internal fun BmAppNavHost(
    navController: NavHostController,
    stage: FeeFirstStage,
    modifier: Modifier = Modifier,
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    LaunchedEffect(stage, currentBackStackEntry) {
        val destination = currentBackStackEntry?.destination ?: return@LaunchedEffect
        val blockedRoute = AllRoutes.firstOrNull { route ->
            destination.hasRoute(route::class) && !stage.canReach(route)
        } ?: return@LaunchedEffect

        val fallback = stage.fallbackRoute()
        if (fallback == blockedRoute) return@LaunchedEffect

        navController.navigate(fallback) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = BmAppRoute.CurrentConditions,
        modifier = modifier,
    ) {
        composable<BmAppRoute.FetchConsumption> {
            FetchConsumptionScreen(
                onScanCupsSelected = {
                    navController.navigate(BmAppRoute.CupsScanner) { launchSingleTop = true }
                },
            )
        }

        composable<BmAppRoute.CupsScanner> {
            CupsScannerScreenHost(
                onBack = { navController.popBackStack() },
                onCupsConfirmed = { navController.popBackStack() },
            )
        }

        composable<BmAppRoute.CurrentConditions> {
            CurrentUserConditionsScreen()
        }

        composable<BmAppRoute.Proposals> {
            ProposalsScreen()
        }

        composable<BmAppRoute.Configuration> {
            ConfigurationScreen()
        }
    }
}

/**
 * Bridges the existing scanner view to the fetch-consumption ViewModel, which owns
 * the study that a confirmed CUPS code kicks off.
 */
@Composable
private fun CupsScannerScreenHost(
    onBack: () -> Unit,
    onCupsConfirmed: () -> Unit,
    viewModel: FetchConsumptionViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CupsScannerView(
        isUploadingReport = uiState.isStudyRunning,
        onBack = onBack,
        onCupsConfirmed = { cupsCode ->
            viewModel.onCupsConfirmed(cupsCode)
            onCupsConfirmed()
        },
    )
}

private val AllRoutes: List<BmAppRoute> = listOf(
    BmAppRoute.FetchConsumption,
    BmAppRoute.CupsScanner,
    BmAppRoute.CurrentConditions,
    BmAppRoute.Proposals,
    BmAppRoute.Configuration,
)
