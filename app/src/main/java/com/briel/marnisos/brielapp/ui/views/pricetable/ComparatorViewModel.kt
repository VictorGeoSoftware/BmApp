package com.briel.marnisos.brielapp.ui.views.pricetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.briel.marnisos.brielapp.domain.models.PriceTablesModel
import com.briel.marnisos.brielapp.domain.usecases.GetPriceTablesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ComparatorViewModel(
    private val getPriceTablesUseCase: GetPriceTablesUseCase
) : ViewModel() {

    init {
        fetchPriceProposalTables()
    }

    private val _tariffName = MutableStateFlow(value = "")
    val tariffName: StateFlow<String> = _tariffName

    private val _annualConsumptionTitle = MutableStateFlow(value = "")
    val annualConsumptionTitle: StateFlow<String> = _annualConsumptionTitle

    private val _totalsTitle = MutableStateFlow(value = "")
    val totalsTitle: StateFlow<String> = _totalsTitle

    private val _powerTermRows = MutableStateFlow<List<Pair<String, String>>>(value = emptyList())
    val powerTermRows: StateFlow<List<Pair<String, String>>> = _powerTermRows

    private val _energyConsumedRows = MutableStateFlow<List<Pair<String, String>>>(value = emptyList())
    val energyConsumedRows: StateFlow<List<Pair<String, String>>> = _energyConsumedRows

    private val _impuestoElectrico = MutableStateFlow(value = "")
    val impuestoElectrico: StateFlow<String> = _impuestoElectrico

    private val _iva = MutableStateFlow(value = "")
    val iva: StateFlow<String> = _iva


    private val _priceTablesModel = MutableStateFlow(value = PriceTablesModel.empty)
    val priceTablesModel: StateFlow<PriceTablesModel> = _priceTablesModel

    private fun fetchPriceProposalTables() {
        viewModelScope.launch {
            getPriceTablesUseCase()
                .onSuccess { tablesInformation ->
                    _impuestoElectrico.value = tablesInformation.impuestoElectrico.toString()
                    _iva.value = tablesInformation.iva.toString()
                }
                .onFailure { error ->
                    println("victor - ViewModel- error: $error")
                    error.printStackTrace()
                }
        }
    }
}
