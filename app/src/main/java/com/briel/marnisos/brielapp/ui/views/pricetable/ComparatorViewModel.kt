package com.briel.marnisos.brielapp.ui.views.pricetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.briel.marnisos.brielapp.domain.models.PriceTablesModel
import com.briel.marnisos.brielapp.domain.models.UserConsumptionModel
import com.briel.marnisos.brielapp.domain.usecases.GetPriceTablesUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetUserConsumptionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ComparatorViewModel(
    private val getPriceTablesUseCase: GetPriceTablesUseCase,
    private val getUserConsumptionUseCase: GetUserConsumptionUseCase
) : ViewModel() {

    init {
        fetchPriceProposalTables()
        getUSerConsumptionData()
    }

    private val _tariffName = MutableStateFlow(value = "")
    val tariffName: StateFlow<String> = _tariffName

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

    private fun getUSerConsumptionData() {
        viewModelScope.launch {
            getUserConsumptionUseCase()
                .onSuccess { userConsumption ->
                    _tariffName.value = userConsumption.feeType
                    _powerTermRows.value = createPowerTermRows(userConsumption)
                    _energyConsumedRows.value = createConsumedEnergyRows(userConsumption)

                }
                .onFailure { error ->
                    println("victor - ViewModel- error: $error")
                    error.printStackTrace()
                }
        }
    }

    /**
     * Creates a list of pairs representing the annual consumption for each phase.
     *
     * @param userConsumption The user consumption data.
     * @return A list of pairs representing the annual consumption for each phase.
     * It cover the Término de Potencia section.
     * TODO:: this data need to be converted and mapped in back end. Create task for it.
     */
    private fun createConsumedEnergyRows(
        userConsumption: UserConsumptionModel
    ): List<Pair<String, String>> {
        val p1 = if (userConsumption.annualConsumptionP1 > 0.0) {
            Pair("P1", userConsumption.annualConsumptionP1.toString())
        } else null

        val p2 = if (userConsumption.annualConsumptionP2 > 0.0) {
            Pair("P2", userConsumption.annualConsumptionP2.toString())
        } else null

        val p3 = if (userConsumption.annualConsumptionP3 > 0.0) {
            Pair("P3", userConsumption.annualConsumptionP3.toString())
        } else null

        val p4 = if (userConsumption.annualConsumptionP4 > 0.0) {
            Pair("P4", userConsumption.annualConsumptionP4.toString())
        } else null

        val p5 = if (userConsumption.annualConsumptionP5 > 0.0) {
            Pair("P5", userConsumption.annualConsumptionP5.toString())
        } else null

        val p6 = if (userConsumption.annualConsumptionP6 > 0.0) {
            Pair("P6", userConsumption.annualConsumptionP6.toString())
        } else null

        return listOfNotNull(p1, p2, p3, p4, p5, p6)
    }

    /**
     * Creates a list of pairs representing the subscribed power for each phase.
     *
     * @param userConsumption The user consumption data.
     * @return A list of pairs representing the subscribed power for each phase.
     * It cover the Término de Potencia section.
     * TODO:: this data need to be converted and mapped in back end. Create task for it.
     */
    private fun createPowerTermRows(
        userConsumption: UserConsumptionModel
    ): List<Pair<String, String>> {
        val p1 = if (userConsumption.subscribedPowerP1 > 0.0) {
            Pair("P1", userConsumption.subscribedPowerP1.toString())
        } else null

        val p2 = if (userConsumption.subscribedPowerP2 > 0.0) {
            Pair("P2", userConsumption.subscribedPowerP2.toString())
        } else null

        val p3 = if (userConsumption.subscribedPowerP3 > 0.0) {
            Pair("P3", userConsumption.subscribedPowerP3.toString())
        } else null

        val p4 = if (userConsumption.subscribedPowerP4 > 0.0) {
            Pair("P4", userConsumption.subscribedPowerP4.toString())
        } else null

        val p5 = if (userConsumption.subscribedPowerP5 > 0.0) {
            Pair("P5", userConsumption.subscribedPowerP5.toString())
        } else null

        val p6 = if (userConsumption.subscribedPowerP6 > 0.0) {
            Pair("P6", userConsumption.subscribedPowerP6.toString())
        } else null

        return listOfNotNull(p1, p2, p3, p4, p5, p6)
    }
}
