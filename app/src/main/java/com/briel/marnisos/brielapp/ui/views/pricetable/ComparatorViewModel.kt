package com.briel.marnisos.brielapp.ui.views.pricetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.briel.marnisos.brielapp.domain.models.ComparatorReportColumnModel
import com.briel.marnisos.brielapp.domain.models.ComparatorReportPdfModel
import com.briel.marnisos.brielapp.domain.models.ComparatorReportPeriodIntValueModel
import com.briel.marnisos.brielapp.domain.models.ComparatorReportPeriodValueModel
import com.briel.marnisos.brielapp.domain.models.ComparatorReportProposalModel
import com.briel.marnisos.brielapp.domain.models.CurrentUserConditionsModel
import com.briel.marnisos.brielapp.domain.models.JobStatusType
import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel
import com.briel.marnisos.brielapp.domain.usecases.ClearCurrentUserConditionsUseCase
import com.briel.marnisos.brielapp.domain.usecases.ClearLastCompletedJobIdUseCase
import com.briel.marnisos.brielapp.domain.usecases.GenerateComparatorReportPdfUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetJobResultUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetJobStatusUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetLastCompletedJobIdUseCase
import com.briel.marnisos.brielapp.domain.usecases.ObserveCurrentUserConditionsUseCase
import com.briel.marnisos.brielapp.domain.usecases.PersistLastCompletedJobIdUseCase
import com.briel.marnisos.brielapp.domain.usecases.RefreshConsumptionReportUseCase
import com.briel.marnisos.brielapp.domain.usecases.SubmitConsumptionReportJobUseCase
import com.briel.marnisos.brielapp.notifications.PriceUpdatesEventBus
import com.briel.marnisos.brielapp.ui.views.comparator.customerconditions.CustomerConditionsUiState
import com.briel.marnisos.brielapp.ui.views.pricetable.export.ComparatorPdfFileStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import kotlin.math.round

class ComparatorViewModel(
    private val submitConsumptionReportJobUseCase: SubmitConsumptionReportJobUseCase,
    private val getJobStatusUseCase: GetJobStatusUseCase,
    private val getJobResultUseCase: GetJobResultUseCase,
    private val refreshConsumptionReportUseCase: RefreshConsumptionReportUseCase,
    private val persistLastCompletedJobIdUseCase: PersistLastCompletedJobIdUseCase,
    private val getLastCompletedJobIdUseCase: GetLastCompletedJobIdUseCase,
    private val clearLastCompletedJobIdUseCase: ClearLastCompletedJobIdUseCase,
    private val clearCurrentUserConditionsUseCase: ClearCurrentUserConditionsUseCase,
    private val observeCurrentUserConditionsUseCase: ObserveCurrentUserConditionsUseCase,
    private val generateComparatorReportPdfUseCase: GenerateComparatorReportPdfUseCase,
    private val proposalCalculationHelper: ProposalCalculationHelper,
    private val comparatorPdfFileStore: ComparatorPdfFileStore
) : ViewModel() {

    private var lastCompletedJobId: String? = null
    private var ivaPercentFromBackend: Double = 0.0
    private var impuestoElectricoPercentFromBackend: Double = 0.0
    private var baseProposalPriceModelList: List<ProposalPriceModel> = emptyList()
    private var cachedCurrentUserConditions: CurrentUserConditionsModel? = null
    private var customerTotalAnnualPriceValue: Double = 0.0
    private var supplyCupsCode: String = ""
    private var isSupplyHolderOverriddenByUser: Boolean = false
    private var isSupplyAddressOverriddenByUser: Boolean = false
    private var lastBackendSupplyHolder: String = ""
    private var lastBackendSupplyAddress: String = ""

    private val _supplyHolder = MutableStateFlow(value = "")
    val supplyHolder: StateFlow<String> = _supplyHolder

    private val _supplyAddress = MutableStateFlow(value = "")
    val supplyAddress: StateFlow<String> = _supplyAddress

    private val _tariffName = MutableStateFlow(value = "")
    val tariffName: StateFlow<String> = _tariffName

    private val _powerTermRows = MutableStateFlow<List<Pair<String, Double>>>(value = emptyList())
    val powerTermRows: StateFlow<List<Pair<String, Double>>> = _powerTermRows

    private val _energyConsumedRows = MutableStateFlow<List<Pair<String, Int>>>(value = emptyList())
    val energyConsumedRows: StateFlow<List<Pair<String, Int>>> = _energyConsumedRows

    private val _impuestoElectrico = MutableStateFlow(value = "")
    val impuestoElectrico: StateFlow<String> = _impuestoElectrico

    private val _iva = MutableStateFlow(value = "")
    val iva: StateFlow<String> = _iva

    private val _proposalPriceModelList = MutableStateFlow<List<ProposalPriceModel>>(value = emptyList())
    val proposalPriceModelList: StateFlow<List<ProposalPriceModel>> = _proposalPriceModelList

    private val _proposalAnnualPriceDeltaByTitle = MutableStateFlow<Map<String, Double>>(value = emptyMap())
    val proposalAnnualPriceDeltaByTitle: StateFlow<Map<String, Double>> = _proposalAnnualPriceDeltaByTitle

    private val _proposalAnnualSavingsPercentageByTitle = MutableStateFlow<Map<String, Int>>(value = emptyMap())
    val proposalAnnualSavingsPercentageByTitle: StateFlow<Map<String, Int>> = _proposalAnnualSavingsPercentageByTitle

    private val _proposalFixedAmountByTitle = MutableStateFlow<Map<String, String>>(value = emptyMap())
    val proposalFixedAmountByTitle: StateFlow<Map<String, String>> = _proposalFixedAmountByTitle

    private val _proposalVisibilityByTitle = MutableStateFlow<Map<String, Boolean>>(value = emptyMap())
    val proposalVisibilityByTitle: StateFlow<Map<String, Boolean>> = _proposalVisibilityByTitle

    private val _customerConditionsUiState = MutableStateFlow(CustomerConditionsUiState())
    val customerConditionsUiState: StateFlow<CustomerConditionsUiState> = _customerConditionsUiState

    private val _isUploadingReport = MutableStateFlow(value = false)
    val isUploadingReport: StateFlow<Boolean> = _isUploadingReport

    private val _uploadStatus = MutableStateFlow<String?>(value = null)
    val uploadStatus: StateFlow<String?> = _uploadStatus

    private val _uploadError = MutableStateFlow<String?>(value = null)
    val uploadError: StateFlow<String?> = _uploadError

    private val _isGeneratingPdf = MutableStateFlow(value = false)
    val isGeneratingPdf: StateFlow<Boolean> = _isGeneratingPdf

    private val _pdfExportError = MutableStateFlow<String?>(value = null)
    val pdfExportError: StateFlow<String?> = _pdfExportError

    private val _generatedPdfFile = MutableSharedFlow<File>(extraBufferCapacity = 1)
    val generatedPdfFile: SharedFlow<File> = _generatedPdfFile

    init {
        viewModelScope.launch {
            restoreLastCompletedJobIfAvailable()
        }

        viewModelScope.launch {
            PriceUpdatesEventBus.events.collect {
                refreshLatestProposalsIfAvailable()
            }
        }

        viewModelScope.launch {
            observeCurrentUserConditionsUseCase().collect { currentUserConditions ->
                cachedCurrentUserConditions = currentUserConditions
                recomputeCustomerConditionsUiState()
            }
        }
    }

    private suspend fun restoreLastCompletedJobIfAvailable() {
        val persistedJobId = getLastCompletedJobIdUseCase() ?: return
        lastCompletedJobId = persistedJobId
        fetchJobResult(persistedJobId)
    }

    /**
     * Uploads a PDF consumption report and processes it through the backend using async job processing
     * @param pdfFile The PDF file to upload
     */
    fun uploadConsumptionReport(pdfFile: File) {
        viewModelScope.launch {
            _isUploadingReport.value = true
            _uploadStatus.value = "Uploading PDF..."
            _uploadError.value = null

            // Step 1: Submit the job
            submitConsumptionReportJobUseCase(pdfFile)
                .onSuccess { jobSubmission ->
                    _uploadStatus.value = "Processing report..."

                    // Step 2: Poll for job completion
                    pollJobStatus(jobSubmission.jobId)
                }
                .onFailure { error ->
                    _uploadError.value = "Failed to upload PDF: ${error.message}"
                    _uploadStatus.value = null
                    _isUploadingReport.value = false
                }
        }
    }

    fun resetCurrentUserConditionsAndProposals() {
        viewModelScope.launch {
            clearCurrentUserConditionsUseCase()
        }

        cachedCurrentUserConditions = null
        _customerConditionsUiState.value = CustomerConditionsUiState()

        baseProposalPriceModelList = emptyList()
        _supplyHolder.value = ""
        _supplyAddress.value = ""
        isSupplyHolderOverriddenByUser = false
        isSupplyAddressOverriddenByUser = false
        lastBackendSupplyHolder = ""
        lastBackendSupplyAddress = ""
        supplyCupsCode = ""
        _proposalPriceModelList.value = emptyList()
        _proposalAnnualPriceDeltaByTitle.value = emptyMap()
        _proposalAnnualSavingsPercentageByTitle.value = emptyMap()
        _proposalFixedAmountByTitle.value = emptyMap()
        _proposalVisibilityByTitle.value = emptyMap()
    }

    /**
     * Polls the job status until completion or failure
     * @param jobId The job ID to poll
     */
    private suspend fun pollJobStatus(
        jobId: String,
        maxAttempts: Int = 60, // 60 attempts * 2 seconds = 2 minutes timeout
        delayMillis: Long = 3000L // Poll every 2 seconds
    ) {
        var attempts = 0

        while (attempts < maxAttempts) {
            delay(delayMillis)
            attempts++

            getJobStatusUseCase(jobId)
                .onSuccess { jobStatus ->
                    when (jobStatus.status) {
                        JobStatusType.COMPLETED -> {
                            _uploadStatus.value = "Fetching results..."
                            fetchJobResult(jobId)
                            return
                        }
                        JobStatusType.FAILED -> {
                            _uploadError.value = "Processing failed. Please try again."
                            _uploadStatus.value = null
                            _isUploadingReport.value = false
                            return
                        }
                        JobStatusType.PROCESSING -> {
                            _uploadStatus.value = "Processing report... (${attempts * 2}s)"
                        }
                        JobStatusType.PENDING -> {
                            _uploadStatus.value = "Waiting in queue..."
                        }
                    }
                }
                .onFailure { error ->
                    _uploadError.value = "Failed to check status: ${error.message}"
                    _uploadStatus.value = null
                    _isUploadingReport.value = false
                    return
                }
        }

        // Timeout reached
        _uploadError.value = "Processing timeout. Please try again."
        _uploadStatus.value = null
        _isUploadingReport.value = false
    }

    /**
     * Fetches the final result of a completed job
     * @param jobId The job ID to fetch results for
     */
    private suspend fun fetchJobResult(jobId: String) {
        getJobResultUseCase(jobId)
            .onSuccess { report ->
                lastCompletedJobId = jobId
                persistLastCompletedJobIdUseCase(jobId)
                // Update the consumption data with the cleaned data from the report
                _tariffName.value = report.consumptionData.feeType
                _impuestoElectrico.value = report.impuestoElectrico.toPercentString()
                _iva.value = report.iva.toPercentString()
                _uploadStatus.value = "Complete!"
                _isUploadingReport.value = false

                _powerTermRows.value = report.consumptionData.subscribedPowerValues()

                _energyConsumedRows.value = report.consumptionData.annualConsumptionValues().map { item ->
                    Pair(item.first, item.second.toInt())
                }
                val backendSupplyHolder = ""
                val backendSupplyAddress = ""
                val shouldApplyBackendSupplyHolder = !isSupplyHolderOverriddenByUser && (
                    _supplyHolder.value.isBlank() || _supplyHolder.value == lastBackendSupplyHolder
                )
                val shouldApplyBackendSupplyAddress = !isSupplyAddressOverriddenByUser && (
                    _supplyAddress.value.isBlank() || _supplyAddress.value == lastBackendSupplyAddress
                )

                if (shouldApplyBackendSupplyHolder) {
                    _supplyHolder.value = backendSupplyHolder
                }
                if (shouldApplyBackendSupplyAddress) {
                    _supplyAddress.value = backendSupplyAddress
                }

                lastBackendSupplyHolder = backendSupplyHolder
                lastBackendSupplyAddress = backendSupplyAddress
                supplyCupsCode = report.userData.cupsCode.ifBlank { report.consumptionData.cups }

                // Proposals now come directly from backend
                ivaPercentFromBackend = report.iva
                impuestoElectricoPercentFromBackend = report.impuestoElectrico
                setBaseProposals(report.proposals)
                synchronizeProposalVisibility(report.proposals)
                recomputeCustomerConditionsUiState()
                viewModelScope.launch {
                    delay(2000)
                    _uploadStatus.value = null
                }
            }
            .onFailure { error ->
                if (error.isJobExpiredOrNotFound()) {
                    clearPersistedLastCompletedJobId()
                }
                _uploadError.value = "Failed to fetch results: ${error.message}"
                _uploadStatus.value = null
                _isUploadingReport.value = false
            }
    }

    private suspend fun refreshLatestProposalsIfAvailable() {
        val jobId = lastCompletedJobId ?: return

        refreshConsumptionReportUseCase(jobId)
            .onSuccess { report ->
                ivaPercentFromBackend = report.iva
                impuestoElectricoPercentFromBackend = report.impuestoElectrico
                setBaseProposals(report.proposals)
                synchronizeProposalVisibility(report.proposals)
                _impuestoElectrico.value = report.impuestoElectrico.toPercentString()
                _iva.value = report.iva.toPercentString()
                recomputeCustomerConditionsUiState()
            }
            .onFailure { error ->
                if (error.isJobExpiredOrNotFound()) {
                    clearPersistedLastCompletedJobId()
                    return
                }
                _uploadError.value = "Failed to refresh prices: ${error.message}"
            }
    }

    private suspend fun clearPersistedLastCompletedJobId() {
        clearLastCompletedJobIdUseCase()
        lastCompletedJobId = null
    }

    private fun Throwable.isJobExpiredOrNotFound(): Boolean {
        val message = message.orEmpty()
        return message.contains("404") || message.contains("not found", ignoreCase = true)
    }

    private fun Double.toPercentString(): String {
        return String.format(Locale.US, "%.2f %%", this)
    }

    private fun synchronizeProposalVisibility(proposals: List<ProposalPriceModel>) {
        val currentVisibility = _proposalVisibilityByTitle.value
        _proposalVisibilityByTitle.value = proposals.associate { proposal ->
            proposal.proposalTitle to (currentVisibility[proposal.proposalTitle] ?: true)
        }
    }

    fun setProposalVisibility(proposalTitle: String, isVisible: Boolean) {
        val currentVisibility = _proposalVisibilityByTitle.value
        if (!currentVisibility.containsKey(proposalTitle)) return

        _proposalVisibilityByTitle.value = currentVisibility.toMutableMap().apply {
            this[proposalTitle] = isVisible
        }
    }

    fun updateProposalFixedAmount(proposalTitle: String, fixedAmountInput: String) {
        val currentInputs = _proposalFixedAmountByTitle.value
        if (!currentInputs.containsKey(proposalTitle)) return

        _proposalFixedAmountByTitle.value = currentInputs.toMutableMap().apply {
            this[proposalTitle] = fixedAmountInput
        }

        recomputeProposalPriceModels()
    }

    private fun setBaseProposals(proposals: List<ProposalPriceModel>) {
        baseProposalPriceModelList = proposals
        synchronizeProposalFixedAmountInputs(proposals)
        recomputeProposalPriceModels()
    }

    private fun synchronizeProposalFixedAmountInputs(proposals: List<ProposalPriceModel>) {
        val currentInputs = _proposalFixedAmountByTitle.value
        _proposalFixedAmountByTitle.value = proposals.associate { proposal ->
            proposal.proposalTitle to (currentInputs[proposal.proposalTitle] ?: "")
        }
    }

    private fun recomputeProposalPriceModels() {
        val fixedAmountsByTitle = _proposalFixedAmountByTitle.value
        _proposalPriceModelList.value = baseProposalPriceModelList.map { proposal ->
            val inputValue = fixedAmountsByTitle[proposal.proposalTitle].orEmpty()
            val additionalAmount = parseAmountInput(inputValue)

            proposalCalculationHelper.recalculateProposalWithAdditionalAmount(
                proposal = proposal,
                additionalAmount = additionalAmount,
                ivaPercent = ivaPercentFromBackend
            )
        }

        recomputeProposalAnnualPriceDeltas()
    }

    private fun recomputeProposalAnnualPriceDeltas() {
        if (cachedCurrentUserConditions == null) {
            _proposalAnnualPriceDeltaByTitle.value = emptyMap()
            _proposalAnnualSavingsPercentageByTitle.value = emptyMap()
            return
        }

        val annualPriceDeltasByTitle = mutableMapOf<String, Double>()
        val annualSavingsPercentagesByTitle = mutableMapOf<String, Int>()

        _proposalPriceModelList.value.forEach { proposal ->
            annualPriceDeltasByTitle[proposal.proposalTitle] = proposalCalculationHelper.calculateAnnualPriceDelta(
                customerTotalAnnualPrice = customerTotalAnnualPriceValue,
                proposalTotalAnnualPrice = proposal.totalAnnualPrice
            )

            annualSavingsPercentagesByTitle[proposal.proposalTitle] = proposalCalculationHelper.calculateAnnualSavingsPercentage(
                customerTotalAnnualPrice = customerTotalAnnualPriceValue,
                proposalTotalAnnualPrice = proposal.totalAnnualPrice
            )
        }

        _proposalAnnualPriceDeltaByTitle.value = annualPriceDeltasByTitle
        _proposalAnnualSavingsPercentageByTitle.value = annualSavingsPercentagesByTitle
    }

    private fun parseAmountInput(input: String): Double {
        if (input.isBlank()) return 0.0
        return input.replace(',', '.').toDoubleOrNull() ?: 0.0
    }

    private fun recomputeCustomerConditionsUiState() {
        val powerRows = _powerTermRows.value
        val energyRows = _energyConsumedRows.value

        if (powerRows.isEmpty() && energyRows.isEmpty()) {
            customerTotalAnnualPriceValue = 0.0
            _customerConditionsUiState.value = CustomerConditionsUiState()
            recomputeProposalAnnualPriceDeltas()
            return
        }

        val currentConditions = cachedCurrentUserConditions
        val powerPriceByPeriod = currentConditions?.powerTermPriceByPeriod.orEmpty()
        val energyPriceByPeriod = currentConditions?.energyPriceByPeriod.orEmpty()
        val extraServicesValue = currentConditions?.extraServices.parseDecimalInput()

        val powerTermItems = powerRows.map { row ->
            powerPriceByPeriod[row.first].parseDecimalInput()
        }
        val consumedEnergyItems = energyRows.map { row ->
            energyPriceByPeriod[row.first].parseDecimalInput()
        }

        val annualPowerTermCost = powerRows
            .zip(powerTermItems)
            .sumOf { (powerData, powerPrice) ->
                powerData.second * powerPrice * 365.0
            }

        val annualEnergyCost = energyRows
            .zip(consumedEnergyItems)
            .sumOf { (energyData, energyPrice) ->
                energyData.second * energyPrice
            }

        val baseCost = annualPowerTermCost + annualEnergyCost
        val ivaAmount = baseCost * (ivaPercentFromBackend / 100.0)
        val electricalTax = baseCost * (impuestoElectricoPercentFromBackend / 100.0)
        val totalAnnualPrice = baseCost + extraServicesValue + ivaAmount + electricalTax
        customerTotalAnnualPriceValue = totalAnnualPrice

        _customerConditionsUiState.value = CustomerConditionsUiState(
            powerTermItems = powerTermItems,
            annualPowerTermCost = annualPowerTermCost.toTwoDecimalsString(),
            consumedEnergyItems = consumedEnergyItems,
            annualEnergyCost = annualEnergyCost.toTwoDecimalsString(),
            extraServices = extraServicesValue.toTwoDecimalsString(),
            electricTax = electricalTax.toTwoDecimalsString(),
            iva = ivaAmount.toTwoDecimalsString(),
            totalAnnualPrice = totalAnnualPrice.toTwoDecimalsString(),
        )

        recomputeProposalAnnualPriceDeltas()
    }

    private fun String?.parseDecimalInput(): Double {
        if (this.isNullOrBlank()) return 0.0
        return this.replace(',', '.').toDoubleOrNull() ?: 0.0
    }

    private fun Double.toTwoDecimalsString(): String {
        val roundedValue = round(this * 100.0) / 100.0
        return String.format(Locale.US, "%.2f", roundedValue)
    }

    fun exportVisibleProposalsAsPdf() {
        if (_isGeneratingPdf.value) return

        viewModelScope.launch {
            _isGeneratingPdf.value = true
            _pdfExportError.value = null

            val reportModel = buildComparatorReportModel()
            if (reportModel.proposals.isEmpty()) {
                _pdfExportError.value = "No hay propuestas visibles para exportar"
                _isGeneratingPdf.value = false
                return@launch
            }

            generateComparatorReportPdfUseCase(reportModel)
                .onSuccess { pdfBytes ->
                    comparatorPdfFileStore.store(pdfBytes)
                        .onSuccess { file ->
                            _generatedPdfFile.emit(file)
                        }
                        .onFailure { error ->
                            _pdfExportError.value = "No se pudo preparar el archivo PDF: ${error.message}"
                        }
                }
                .onFailure { error ->
                    _pdfExportError.value = "No se pudo generar el PDF: ${error.message}"
                }

            _isGeneratingPdf.value = false
        }
    }

    private fun buildComparatorReportModel(): ComparatorReportPdfModel {
        val visibleProposals = _proposalPriceModelList.value.filter { proposal ->
            _proposalVisibilityByTitle.value[proposal.proposalTitle] ?: true
        }

        val powerRows = _powerTermRows.value
        val energyRows = _energyConsumedRows.value
        val customerColumnUiState = _customerConditionsUiState.value

        return ComparatorReportPdfModel(
            supplyHolder = _supplyHolder.value.ifBlank { "--" },
            supplyAddress = _supplyAddress.value.ifBlank { "--" },
            cups = supplyCupsCode.ifBlank { "--" },
            tariffName = _tariffName.value,
            powerTermRows = powerRows.map { row ->
                ComparatorReportPeriodValueModel(period = row.first, value = row.second)
            },
            energyConsumedRows = energyRows.map { row ->
                ComparatorReportPeriodIntValueModel(period = row.first, value = row.second)
            },
            iva = _iva.value,
            impuestoElectrico = _impuestoElectrico.value,
            customerConditions = ComparatorReportColumnModel(
                title = "CONDICIONES ACTUALES",
                powerTermItems = customerColumnUiState.powerTermItems,
                annualPowerTermCost = customerColumnUiState.annualPowerTermCost,
                consumedEnergyItems = customerColumnUiState.consumedEnergyItems,
                annualEnergyCost = customerColumnUiState.annualEnergyCost,
                extraServices = customerColumnUiState.extraServices,
                electricalTax = customerColumnUiState.electricTax,
                iva = customerColumnUiState.iva,
                totalAnnualPrice = customerColumnUiState.totalAnnualPrice
            ),
            proposals = visibleProposals.map { proposal ->
                ComparatorReportProposalModel(
                    title = proposal.proposalTitle,
                    powerTermItems = proposal.powerTermItems,
                    annualPowerTermCost = proposal.annualPowerTermCostFormatted,
                    consumedEnergyItems = proposal.consumedEnergyItems,
                    annualEnergyCost = proposal.annualEnergyCostFormatted,
                    extraServices = _proposalFixedAmountByTitle.value[proposal.proposalTitle].orEmpty().ifBlank { "0.00" },
                    electricalTax = proposal.electricalTaxFormatted,
                    iva = proposal.ivaFormatted,
                    totalAnnualPrice = proposal.totalAnnualPriceFormatted,
                    annualPriceDifference = _proposalAnnualPriceDeltaByTitle.value[proposal.proposalTitle]?.toTwoDecimalsString(),
                    annualSavingsPercentage = _proposalAnnualSavingsPercentageByTitle.value[proposal.proposalTitle]
                )
            }
        )
    }

    fun clearPdfExportError() {
        _pdfExportError.value = null
    }

    fun updateSupplyHolder(value: String) {
        isSupplyHolderOverriddenByUser = true
        _supplyHolder.value = value
    }

    fun updateSupplyAddress(value: String) {
        isSupplyAddressOverriddenByUser = true
        _supplyAddress.value = value
    }


    /**
     * Clears any upload error messages
     */
    fun clearUploadError() {
        _uploadError.value = null
    }
}
