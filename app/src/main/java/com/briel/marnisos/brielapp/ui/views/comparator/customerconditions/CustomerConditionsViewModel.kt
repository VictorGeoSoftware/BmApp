package com.briel.marnisos.brielapp.ui.views.comparator.customerconditions

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CustomerConditionsViewModel : ViewModel() {
    private val _powerTermItems = MutableStateFlow<List<Double>>(emptyList())
    val powerTermItems: StateFlow<List<Double>> = _powerTermItems

    private val _annualPowerTermCost = MutableStateFlow("")
    val annualPowerTermCost: StateFlow<String> = _annualPowerTermCost

    private val _consumedEnergyItems = MutableStateFlow<List<Double>>(emptyList())
    val consumedEnergyItems: StateFlow<List<Double>> = _consumedEnergyItems

    private val _annualEnergyCost = MutableStateFlow("")
    val annualEnergyCost: StateFlow<String> = _annualEnergyCost

    private val _extraServices = MutableStateFlow("")
    val extraServices: StateFlow<String> = _extraServices

    private val _electricTax = MutableStateFlow("")
    val electricTax: StateFlow<String> = _electricTax

    private val _iva = MutableStateFlow("")
    val iva: StateFlow<String> = _iva

    private val _totalAnnualPrice = MutableStateFlow("")
    val totalAnnualPrice: StateFlow<String> = _totalAnnualPrice

}
