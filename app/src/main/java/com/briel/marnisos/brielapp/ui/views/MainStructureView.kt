package com.briel.marnisos.brielapp.ui.views

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.briel.marnisos.brielapp.BuildConfig
import com.briel.marnisos.brielapp.R
import com.briel.marnisos.brielapp.ui.navigation.BmAppNavHost
import com.briel.marnisos.brielapp.ui.navigation.BmAppRoute
import com.briel.marnisos.brielapp.ui.views.common.TopActionBar
import com.briel.marnisos.brielapp.ui.views.proposals.ProposalsViewModel
import com.briel.marnisos.brielapp.ui.views.pricetable.export.ComparatorPdfShareManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * Authenticated app shell: drawer, top bar and the navigation graph.
 *
 * Replaces the previous `MainStructureView`, which switched on an enum and hoisted
 * every screen's state through a single composable.
 */
@Composable
fun MainView(
    modifier: Modifier = Modifier,
    onLogoutClicked: () -> Unit = {},
    shellViewModel: AppShellViewModel = koinViewModel(),
    proposalsViewModel: ProposalsViewModel = koinViewModel(),
) {
    val shellState by shellViewModel.uiState.collectAsStateWithLifecycle()
    val proposalsState by proposalsViewModel.uiState.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val pdfShareManager = remember { ComparatorPdfShareManager() }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = remember(currentBackStackEntry) {
        DrawerRoutes.firstOrNull { route ->
            currentBackStackEntry?.destination?.hasRoute(route::class) == true
        } ?: BmAppRoute.CurrentConditions
    }

    LaunchedEffect(proposalsViewModel, context, pdfShareManager) {
        proposalsViewModel.generatedPdfFile.collectLatest { generatedFile ->
            pdfShareManager.sharePdf(context, generatedFile)
        }
    }

    LaunchedEffect(proposalsViewModel, context) {
        proposalsViewModel.pdfExportFailure.collectLatest {
            Toast.makeText(
                context,
                context.getString(R.string.proposals_pdf_export_error),
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.fillMaxHeight()) {
                com.briel.marnisos.brielapp.ui.views.drawer.DrawerContent(
                    selectedRoute = currentRoute,
                    stage = shellState.stage,
                    onDestinationSelected = { route ->
                        scope.launch { drawerState.close() }
                        if (route == BmAppRoute.FetchConsumption) {
                            // R6: reset the study; the gate change drives the navigation.
                            shellViewModel.startNewStudy()
                        } else {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    onLogoutClicked = {
                        scope.launch { drawerState.close() }
                        onLogoutClicked()
                    },
                    versionLabel = BuildConfig.DEPLOY_VERSION,
                )
            }
        },
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            TopActionBar(
                isUploadingReport = shellState.isStudyRunning,
                isGeneratingPdf = proposalsState.isGeneratingPdf,
                showPrintButton = currentRoute == BmAppRoute.Proposals && proposalsState.hasProposals,
                onGeneratePdfClick = proposalsViewModel::exportVisibleProposalsAsPdf,
                showScanShortcut = currentRoute == BmAppRoute.FetchConsumption,
                onScanCupsSelected = {
                    navController.navigate(BmAppRoute.CupsScanner) { launchSingleTop = true }
                },
                onOpenDrawer = { scope.launch { drawerState.open() } },
            )

            BmAppNavHost(
                navController = navController,
                stage = shellState.stage,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }
    }
}

private val DrawerRoutes: List<BmAppRoute> = listOf(
    BmAppRoute.CurrentConditions,
    BmAppRoute.Proposals,
    BmAppRoute.Configuration,
    BmAppRoute.FetchConsumption,
    BmAppRoute.CupsScanner,
)
