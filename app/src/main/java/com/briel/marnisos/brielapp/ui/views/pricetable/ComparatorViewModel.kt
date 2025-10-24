package com.briel.marnisos.brielapp.ui.views.pricetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.briel.marnisos.brielapp.domain.models.ConsumptionReportModel
import com.briel.marnisos.brielapp.domain.models.PriceTablesModel
import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel
import com.briel.marnisos.brielapp.domain.usecases.UploadConsumptionReportUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class ComparatorViewModel(
    private val uploadConsumptionReportUseCase: UploadConsumptionReportUseCase
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


    /**
     * Uploads a PDF consumption report and processes it through the backend
     * @param pdfFile The PDF file to upload
     */
    fun uploadConsumptionReport(pdfFile: File) {
        viewModelScope.launch {
            _isUploadingReport.value = true
            uploadConsumptionReportUseCase(pdfFile)
                .onSuccess { report ->
                    println("victor - received report :: $report")
                    _consumptionReportModel.value = report
                    // Update the consumption data with the cleaned data from the report
                    _tariffName.value = report.consumptionData.feeType
                    // Update filtered price tables
                    _impuestoElectrico.value = report.filteredPrices.impuestoElectrico.toString()
                    _iva.value = report.filteredPrices.iva.toString()
                    _isUploadingReport.value = false
                }
                .onFailure { error ->
                    println("victor - ViewModel - upload error: $error")
                    error.printStackTrace()
                    _isUploadingReport.value = false
                }
        }
    }
}
