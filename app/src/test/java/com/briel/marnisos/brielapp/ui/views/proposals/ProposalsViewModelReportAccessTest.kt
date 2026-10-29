package com.briel.marnisos.brielapp.ui.views.proposals

import com.briel.marnisos.brielapp.domain.models.ComparatorSummaryModel
import com.briel.marnisos.brielapp.domain.models.ReportAccessModel
import com.briel.marnisos.brielapp.domain.repository.ConsumptionSessionRepository
import com.briel.marnisos.brielapp.domain.usecases.CalculateComparatorSummaryUseCase
import com.briel.marnisos.brielapp.domain.usecases.ObserveCurrentUserConditionsUseCase
import com.briel.marnisos.brielapp.domain.usecases.ObserveReportAccessUseCase
import com.briel.marnisos.brielapp.domain.usecases.RefreshReportAccessUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProposalsViewModelReportAccessTest {

    private val access = MutableStateFlow(ReportAccessModel(canRequestComprehensiveReport = false))
    private var refreshCalls = 0
    private val refresh = RefreshReportAccessUseCase { refreshCalls++ }

    private val sessionRepository = mockk<ConsumptionSessionRepository>(relaxed = true) {
        every { session } returns MutableStateFlow(null)
        every { proposalVisibilityByTitle } returns MutableStateFlow(emptyMap())
        every { proposalFixedAmountByTitle } returns MutableStateFlow(emptyMap())
    }
    private val summaryUseCase = mockk<CalculateComparatorSummaryUseCase> {
        every { this@mockk.invoke(any(), any(), any()) } returns ComparatorSummaryModel()
    }

    private lateinit var viewModel: ProposalsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = ProposalsViewModel(
            consumptionSessionRepository = sessionRepository,
            observeCurrentUserConditionsUseCase = ObserveCurrentUserConditionsUseCase { flowOf(null) },
            calculateComparatorSummaryUseCase = summaryUseCase,
            selectUncompetitiveProposalsUseCase = mockk(relaxed = true),
            generateComparatorReportPdfUseCase = mockk(relaxed = true),
            comparatorPdfFileStore = mockk(relaxed = true),
            crashReporter = mockk(relaxed = true),
            analyticsTracker = mockk(relaxed = true),
            observeReportAccessUseCase = ObserveReportAccessUseCase { access },
            refreshReportAccessUseCase = refresh,
            generatePremiumReportPdfUseCase = mockk(relaxed = true),
            forceRefreshReportAccessUseCase = mockk(relaxed = true),
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `T13 starts hidden`() = runTest {
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        assertFalse(viewModel.uiState.value.canRequestComprehensiveReport)
        job.cancel()
    }

    @Test
    fun `T1 T9 T10 ui state follows the repository access`() = runTest {
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        access.value = ReportAccessModel(true)
        assertTrue(viewModel.uiState.value.canRequestComprehensiveReport)
        access.value = ReportAccessModel(false)
        assertFalse(viewModel.uiState.value.canRequestComprehensiveReport)
        access.value = ReportAccessModel(true)
        assertTrue(viewModel.uiState.value.canRequestComprehensiveReport)
        job.cancel()
    }

    @Test
    fun `T14 capable without visible proposals does not show the action`() = runTest {
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        access.value = ReportAccessModel(true)
        assertFalse(viewModel.uiState.value.hasProposals)
        assertFalse(viewModel.uiState.value.showsComprehensiveReportAction)
        job.cancel()
    }

    @Test
    fun `access check trigger refreshes the repository`() {
        viewModel.onAccessCheckTrigger()
        assertEquals(1, refreshCalls)
    }

    @Test
    fun `constructing and observing does not trigger a check by itself`() = runTest {
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        assertEquals(0, refreshCalls)
        job.cancel()
    }

    @Test
    fun `showsComprehensiveReportAction requires proposals and capability`() {
        val proposal = mockk<com.briel.marnisos.brielapp.domain.models.ProposalPriceModel>()
        assertTrue(ProposalsUiState(visibleProposals = listOf(proposal), canRequestComprehensiveReport = true).showsComprehensiveReportAction)
        assertFalse(ProposalsUiState(visibleProposals = listOf(proposal), canRequestComprehensiveReport = false).showsComprehensiveReportAction)
        assertFalse(ProposalsUiState(visibleProposals = emptyList(), canRequestComprehensiveReport = true).showsComprehensiveReportAction)
    }
}
