package com.briel.marnisos.brielapp.ui.views.proposals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.briel.marnisos.brielapp.domain.models.ComparatorReportColumnModel
import com.briel.marnisos.brielapp.domain.models.ComparatorReportPdfModel
import com.briel.marnisos.brielapp.domain.models.ComparatorReportPeriodIntValueModel
import com.briel.marnisos.brielapp.domain.models.ComparatorReportPeriodValueModel
import com.briel.marnisos.brielapp.domain.models.ComparatorReportProposalModel
import com.briel.marnisos.brielapp.domain.models.ComparatorSummaryModel
import com.briel.marnisos.brielapp.domain.models.ConsumptionSessionModel
import com.briel.marnisos.brielapp.domain.monitoring.AnalyticsEvent
import com.briel.marnisos.brielapp.domain.monitoring.AnalyticsFailureReason
import com.briel.marnisos.brielapp.domain.monitoring.AnalyticsTracker
import com.briel.marnisos.brielapp.domain.monitoring.CrashErrorCategory
import com.briel.marnisos.brielapp.domain.monitoring.CrashReporter
import com.briel.marnisos.brielapp.domain.repository.ConsumptionSessionRepository
import com.briel.marnisos.brielapp.domain.usecases.CalculateComparatorSummaryUseCase
import com.briel.marnisos.brielapp.domain.usecases.GenerateComparatorReportPdfUseCase
import com.briel.marnisos.brielapp.domain.usecases.ObserveCurrentUserConditionsUseCase
import com.briel.marnisos.brielapp.domain.usecases.SelectUncompetitiveProposalsUseCase
import com.briel.marnisos.brielapp.ui.views.comparator.customerconditions.CustomerConditionsUiState
import com.briel.marnisos.brielapp.ui.views.pricetable.export.ComparatorPdfFileStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import kotlin.math.round

class ProposalsViewModel(
    private val consumptionSessionRepository: ConsumptionSessionRepository,
    private val observeCurrentUserConditionsUseCase: ObserveCurrentUserConditionsUseCase,
    private val calculateComparatorSummaryUseCase: CalculateComparatorSummaryUseCase,
    private val selectUncompetitiveProposalsUseCase: SelectUncompetitiveProposalsUseCase,
    private val generateComparatorReportPdfUseCase: GenerateComparatorReportPdfUseCase,
    private val comparatorPdfFileStore: ComparatorPdfFileStore,
    private val crashReporter: CrashReporter,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {

    private val _isGeneratingPdf = MutableStateFlow(value = false)

    private val _generatedPdfFile = MutableSharedFlow<File>(extraBufferCapacity = 1)
    val generatedPdfFile: SharedFlow<File> = _generatedPdfFile

    private val _pdfExportFailure = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val pdfExportFailure: SharedFlow<Unit> = _pdfExportFailure

    private var lastTrackedProposalsJobId: String? = null

    val uiState: StateFlow<ProposalsUiState> = combine(
        consumptionSessionRepository.session,
        observeCurrentUserConditionsUseCase(),
        consumptionSessionRepository.proposalVisibilityByTitle,
        consumptionSessionRepository.proposalFixedAmountByTitle,
        _isGeneratingPdf,
    ) { session, conditions, visibilityByTitle, fixedAmountByTitle, isGeneratingPdf ->
        val summary = calculateComparatorSummaryUseCase(session, conditions, fixedAmountByTitle)
        buildUiState(session, summary, visibilityByTitle, fixedAmountByTitle, isGeneratingPdf)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = ProposalsUiState(),
    )

    init {
        // Only while this screen is alive: arriving at Proposals is what applies the rule,
        // and the repository guard keeps it to once per study + customer prices.
        viewModelScope.launch {
            combine(
                consumptionSessionRepository.session,
                observeCurrentUserConditionsUseCase(),
                consumptionSessionRepository.proposalFixedAmountByTitle,
            ) { session, conditions, fixedAmountByTitle ->
                session?.jobId to calculateComparatorSummaryUseCase(
                    session,
                    conditions,
                    fixedAmountByTitle,
                )
            }.collect { (jobId, summary) ->
                hideUncompetitiveProposals(jobId, summary)
                trackProposalsGeneratedOnce(jobId, summary)
            }
        }
    }

    /**
     * Guarded by the job id: this flow re-emits on every fixed-amount keystroke, and
     * without the guard a single study would report dozens of generation events.
     */
    private fun trackProposalsGeneratedOnce(jobId: String?, summary: ComparatorSummaryModel) {
        if (jobId == null || summary.proposals.isEmpty() || jobId == lastTrackedProposalsJobId) return

        lastTrackedProposalsJobId = jobId
        analyticsTracker.track(AnalyticsEvent.ProposalsGenerated(proposalCount = summary.proposals.size))
    }

    private fun hideUncompetitiveProposals(jobId: String?, summary: ComparatorSummaryModel) {
        if (jobId == null || summary.proposals.isEmpty()) return

        consumptionSessionRepository.hideProposalsOnce(
            signature = "$jobId@${summary.customerTotalAnnualPrice}",
            proposalTitles = selectUncompetitiveProposalsUseCase(summary),
        )
    }

    fun onProposalFixedAmountChanged(proposalTitle: String, fixedAmountInput: String) {
        consumptionSessionRepository.setProposalFixedAmount(proposalTitle, fixedAmountInput)
    }

    fun exportVisibleProposalsAsPdf() {
        if (_isGeneratingPdf.value) return

        viewModelScope.launch {
            crashReporter.setScreenContext("proposals")
            crashReporter.setUseCaseContext("export_visible_proposals_pdf")
            _isGeneratingPdf.value = true

            val reportModel = buildComparatorReportModel()
            if (reportModel == null || reportModel.proposals.isEmpty()) {
                analyticsTracker.track(
                    AnalyticsEvent.ProposalPdfExportFailed(AnalyticsFailureReason.INVALID_INPUT)
                )
                _pdfExportFailure.tryEmit(Unit)
                _isGeneratingPdf.value = false
                return@launch
            }

            generateComparatorReportPdfUseCase(reportModel)
                .onSuccess { pdfBytes ->
                    comparatorPdfFileStore.store(pdfBytes)
                        .onSuccess { file ->
                            analyticsTracker.track(
                                AnalyticsEvent.ProposalPdfExported(
                                    proposalCount = reportModel.proposals.size,
                                )
                            )
                            _generatedPdfFile.tryEmit(file)
                        }
                        .onFailure { error -> reportPdfFailure(error, "store_generated_pdf") }
                }
                .onFailure { error -> reportPdfFailure(error, "generate_comparator_pdf") }

            _isGeneratingPdf.value = false
        }
    }

    private fun reportPdfFailure(error: Throwable, operation: String) {
        crashReporter.recordNonFatal(
            throwable = error,
            category = CrashErrorCategory.PDF_EXPORT,
            operation = operation,
        )
        analyticsTracker.track(
            AnalyticsEvent.ProposalPdfExportFailed(AnalyticsFailureReason.UNKNOWN)
        )
        _pdfExportFailure.tryEmit(Unit)
    }

    private fun buildUiState(
        session: ConsumptionSessionModel?,
        summary: ComparatorSummaryModel,
        visibilityByTitle: Map<String, Boolean>,
        fixedAmountByTitle: Map<String, String>,
        isGeneratingPdf: Boolean,
    ): ProposalsUiState {
        if (session == null) return ProposalsUiState(isGeneratingPdf = isGeneratingPdf)

        val visibleProposals = summary.proposals.filter { proposal ->
            visibilityByTitle[proposal.proposalTitle] ?: true
        }
        val bestProposal = summary.bestProposalAmong(visibleProposals)

        return ProposalsUiState(
            tariffName = session.tariffName,
            powerTermRows = session.powerTermRows,
            energyConsumedRows = session.energyConsumedRows,
            ivaLabel = session.ivaPercent.toPercentLabel(),
            electricTaxLabel = session.electricTaxPercent.toPercentLabel(),
            visibleProposals = visibleProposals,
            annualPriceDeltaByTitle = summary.annualPriceDeltaByTitle,
            annualSavingsPercentageByTitle = summary.annualSavingsPercentageByTitle,
            fixedAmountByTitle = fixedAmountByTitle,
            customerConditions = summary.customerConditions.toUiState(),
            bestProposalTitle = bestProposal?.proposalTitle,
            bestProposalAnnualSaving = bestProposal
                ?.let { proposal -> summary.annualPriceDeltaByTitle[proposal.proposalTitle] }
                ?.toTwoDecimals(),
            isGeneratingPdf = isGeneratingPdf,
        )
    }

    private fun buildComparatorReportModel(): ComparatorReportPdfModel? {
        val state = uiState.value
        val session = consumptionSessionRepository.session.value ?: return null

        return ComparatorReportPdfModel(
            supplyHolder = session.supplyHolder.ifBlank { PLACEHOLDER },
            supplyAddress = session.supplyAddress.ifBlank { PLACEHOLDER },
            cups = session.supplyCupsCode.ifBlank { PLACEHOLDER },
            tariffName = session.tariffName,
            powerTermRows = session.powerTermRows.map { row ->
                ComparatorReportPeriodValueModel(period = row.first, value = row.second)
            },
            energyConsumedRows = session.energyConsumedRows.map { row ->
                ComparatorReportPeriodIntValueModel(period = row.first, value = row.second)
            },
            iva = state.ivaLabel,
            impuestoElectrico = state.electricTaxLabel,
            customerConditions = ComparatorReportColumnModel(
                title = CUSTOMER_COLUMN_TITLE,
                powerTermItems = state.customerConditions.powerTermItems,
                annualPowerTermCost = state.customerConditions.annualPowerTermCost,
                consumedEnergyItems = state.customerConditions.consumedEnergyItems,
                annualEnergyCost = state.customerConditions.annualEnergyCost,
                extraServices = state.customerConditions.extraServices,
                electricalTax = state.customerConditions.electricTax,
                iva = state.customerConditions.iva,
                totalAnnualPrice = state.customerConditions.totalAnnualPrice,
            ),
            proposals = state.visibleProposals.map { proposal ->
                ComparatorReportProposalModel(
                    title = proposal.proposalTitle,
                    powerTermItems = proposal.powerTermItems,
                    annualPowerTermCost = proposal.annualPowerTermCostFormatted,
                    consumedEnergyItems = proposal.consumedEnergyItems,
                    annualEnergyCost = proposal.annualEnergyCostFormatted,
                    extraServices = state.fixedAmountByTitle[proposal.proposalTitle]
                        .orEmpty()
                        .ifBlank { DEFAULT_AMOUNT },
                    electricalTax = proposal.electricalTaxFormatted,
                    iva = proposal.ivaFormatted,
                    totalAnnualPrice = proposal.totalAnnualPriceFormatted,
                    annualPriceDifference = state.annualPriceDeltaByTitle[proposal.proposalTitle]
                        ?.toTwoDecimals(),
                    annualSavingsPercentage = state.annualSavingsPercentageByTitle[proposal.proposalTitle],
                )
            },
        )
    }

    private fun com.briel.marnisos.brielapp.domain.models.CustomerConditionsColumnModel.toUiState() =
        CustomerConditionsUiState(
            powerTermItems = powerTermItems,
            annualPowerTermCost = annualPowerTermCost,
            consumedEnergyItems = consumedEnergyItems,
            annualEnergyCost = annualEnergyCost,
            extraServices = extraServices,
            electricTax = electricTax,
            iva = iva,
            totalAnnualPrice = totalAnnualPrice,
        )

    private fun Double.toPercentLabel(): String = String.format(Locale.US, "%.2f %%", this)

    private fun Double.toTwoDecimals(): String {
        val roundedValue = round(this * 100.0) / 100.0
        return String.format(Locale.US, "%.2f", roundedValue)
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
        const val PLACEHOLDER = "--"
        const val DEFAULT_AMOUNT = "0.00"
        const val CUSTOMER_COLUMN_TITLE = "CONDICIONES ACTUALES"
    }
}
