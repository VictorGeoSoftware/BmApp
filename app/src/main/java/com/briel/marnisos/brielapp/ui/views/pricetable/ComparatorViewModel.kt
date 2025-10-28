package com.briel.marnisos.brielapp.ui.views.pricetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.briel.marnisos.brielapp.domain.models.ConsumptionReportModel
import com.briel.marnisos.brielapp.domain.models.JobStatusType
import com.briel.marnisos.brielapp.domain.models.PriceTablesModel
import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel
import com.briel.marnisos.brielapp.domain.usecases.GetJobResultUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetJobStatusUseCase
import com.briel.marnisos.brielapp.domain.usecases.SubmitConsumptionReportJobUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class ComparatorViewModel(
    private val submitConsumptionReportJobUseCase: SubmitConsumptionReportJobUseCase,
    private val getJobStatusUseCase: GetJobStatusUseCase,
    private val getJobResultUseCase: GetJobResultUseCase
) : ViewModel() {


    private val _tariffName = MutableStateFlow(value = "")
    val tariffName: StateFlow<String> = _tariffName

    private val _powerTermRows = MutableStateFlow<List<Pair<String, String>>>(value = emptyList())
    val powerTermRows: StateFlow<List<Pair<String, String>>> = _powerTermRows

    private val _energyConsumedRows = MutableStateFlow<List<Pair<String, String>>>(value = emptyList())
    val energyConsumedRows: StateFlow<List<Pair<String, String>>> = _energyConsumedRows

    private val _impuestoElectrico = MutableStateFlow(value = "")
    val impuestoElectrico: StateFlow<String> = _impuestoElectrico

    private val _iva = MutableStateFlow(value = "")
    val iva: StateFlow<String> = _iva

    private val _proposalPriceModelList = MutableStateFlow<List<ProposalPriceModel>>(value = emptyList())
    val proposalPriceModel: StateFlow<List<ProposalPriceModel>> = _proposalPriceModelList


    private val _priceTablesModel = MutableStateFlow(value = PriceTablesModel.empty)
    val priceTablesModel: StateFlow<PriceTablesModel> = _priceTablesModel

    private val _consumptionReportModel = MutableStateFlow<ConsumptionReportModel?>(value = null)
    val consumptionReportModel: StateFlow<ConsumptionReportModel?> = _consumptionReportModel

    private val _isUploadingReport = MutableStateFlow(value = false)
    val isUploadingReport: StateFlow<Boolean> = _isUploadingReport

    private val _uploadStatus = MutableStateFlow<String?>(value = null)
    val uploadStatus: StateFlow<String?> = _uploadStatus

    private val _uploadError = MutableStateFlow<String?>(value = null)
    val uploadError: StateFlow<String?> = _uploadError

    init {
        viewModelScope.launch {
            fetchJobResult("a4a4097c-dad6-48d3-83fa-21cbe7e6559b")
        }
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
                    println("victor - Job submitted with ID: ${jobSubmission.jobId}")
                    _uploadStatus.value = "Processing report..."
                    
                    // Step 2: Poll for job completion
                    pollJobStatus(jobSubmission.jobId)
                }
                .onFailure { error ->
                    println("victor - ViewModel - upload error: $error")
                    error.printStackTrace()
                    _uploadError.value = "Failed to upload PDF: ${error.message}"
                    _uploadStatus.value = null
                    _isUploadingReport.value = false
                }
        }
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
                    println("victor - Job status: ${jobStatus.status} (attempt $attempts)")
                    
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
                    println("victor - Error polling job status: $error")
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
                println("victor - received report :: $report")
                _consumptionReportModel.value = report
                // Update the consumption data with the cleaned data from the report
                _tariffName.value = report.consumptionData.feeType
                // Update filtered price tables
                _impuestoElectrico.value = report.filteredPrices.impuestoElectrico.toString()
                _iva.value = report.filteredPrices.iva.toString()
                _uploadStatus.value = "Complete!"
                _isUploadingReport.value = false

                _powerTermRows.value = report.consumptionData.annualConsumptionValues()
                _energyConsumedRows.value = report.consumptionData.subscribedPowerValues()
                
                // Clear status after a short delay
                viewModelScope.launch {
                    delay(2000)
                    _uploadStatus.value = null
                }
            }
            .onFailure { error ->
                println("victor - Error fetching job result: $error")
                _uploadError.value = "Failed to fetch results: ${error.message}"
                _uploadStatus.value = null
                _isUploadingReport.value = false
            }
    }

    /**
     * Clears any upload error messages
     */
    fun clearUploadError() {
        _uploadError.value = null
    }
}
