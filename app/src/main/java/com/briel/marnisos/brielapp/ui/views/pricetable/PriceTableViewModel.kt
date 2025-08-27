package com.briel.marnisos.brielapp.ui.views.pricetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.briel.marnisos.brielapp.domain.models.PriceTables
import com.briel.marnisos.brielapp.domain.usecases.GetPriceTablesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PriceTableViewModel(
    private val getPriceTablesUseCase: GetPriceTablesUseCase
) : ViewModel() {

    init {
        fetchPriceTables()
    }

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
