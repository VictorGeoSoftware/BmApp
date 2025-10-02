package com.briel.marnisos.brielapp.ui.views.pricetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.briel.marnisos.brielapp.domain.models.PriceTables
import com.briel.marnisos.brielapp.domain.usecases.GetPriceTablesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ComparatorViewModel(
    private val getPriceTablesUseCase: GetPriceTablesUseCase
) : ViewModel() {

    init {
        fetchPriceTables()
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

    private val _extraServices = MutableStateFlow<List<Pair<String, String>>>(value = emptyList())
    val extraServices: StateFlow<List<Pair<String, String>>> = _extraServices

    private val _priceTables = MutableStateFlow(value = PriceTables.empty)
    val priceTables: StateFlow<PriceTables> = _priceTables

    private fun fetchPriceTables() {
        viewModelScope.launch {
            getPriceTablesUseCase()
                .onSuccess { tables ->
                    tables.firstOrNull()?.let { table ->
                        _priceTables.value = table
                    }
                }
                .onFailure { error ->
                    println("victor - ViewModel- error: $error")
                    error.printStackTrace()
                }
        }
    }
}
