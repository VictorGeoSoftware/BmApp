package com.briel.marnisos.brielapp.ui.views.currentuserconditions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.briel.marnisos.brielapp.domain.models.CurrentUserConditionsModel
import com.briel.marnisos.brielapp.domain.usecases.ObserveCurrentUserConditionsUseCase
import com.briel.marnisos.brielapp.domain.usecases.PersistCurrentUserConditionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CurrentUserConditionsViewModel(
    private val observeCurrentUserConditionsUseCase: ObserveCurrentUserConditionsUseCase,
    private val persistCurrentUserConditionsUseCase: PersistCurrentUserConditionsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurrentUserConditionsFormState())
    val uiState: StateFlow<CurrentUserConditionsFormState> = _uiState

    init {
        viewModelScope.launch {
            observeCurrentUserConditionsUseCase().collect { cachedConditions ->
                cachedConditions ?: return@collect

                _uiState.update { current ->
                    current.copy(
                        powerTermRows = mergeWithCurrentOrder(
                            currentRows = current.powerTermRows,
                            incomingValues = cachedConditions.powerTermPriceByPeriod,
                        ),
                        energyConsumedRows = mergeWithCurrentOrder(
                            currentRows = current.energyConsumedRows,
                            incomingValues = cachedConditions.energyPriceByPeriod,
                        ),
                        extraServices = cachedConditions.extraServices,
                    )
                }
            }
        }
    }

    fun ensurePeriods(
        powerPeriods: List<String>,
        energyPeriods: List<String>,
    ) {
        _uiState.update { current ->
            val powerValueByPeriod = current.powerTermRows.toMap()
            val energyValueByPeriod = current.energyConsumedRows.toMap()

            current.copy(
                powerTermRows = powerPeriods.map { period ->
                    period to powerValueByPeriod[period].orEmpty()
                },
                energyConsumedRows = energyPeriods.map { period ->
                    period to energyValueByPeriod[period].orEmpty()
                },
            )
        }

        persistCurrentState()
    }

    fun onPowerTermValueChanged(period: String, value: String) {
        if (!isValidDecimalInput(value)) return

        _uiState.update { current ->
            current.copy(
                powerTermRows = current.powerTermRows.map { row ->
                    if (row.first == period) {
                        period to value
                    } else {
                        row
                    }
                }
            )
        }

        persistCurrentState()
    }

    fun onEnergyValueChanged(period: String, value: String) {
        if (!isValidDecimalInput(value)) return

        _uiState.update { current ->
            current.copy(
                energyConsumedRows = current.energyConsumedRows.map { row ->
                    if (row.first == period) {
                        period to value
                    } else {
                        row
                    }
                }
            )
        }

        persistCurrentState()
    }

    fun onExtraServicesChanged(value: String) {
        if (!isValidDecimalInput(value)) return

        _uiState.update { current ->
            current.copy(extraServices = value)
        }

        persistCurrentState()
    }

    private fun mergeWithCurrentOrder(
        currentRows: List<Pair<String, String>>,
        incomingValues: Map<String, String>,
    ): List<Pair<String, String>> {
        if (incomingValues.isEmpty()) return currentRows

        if (currentRows.isEmpty()) {
            return incomingValues.map { (period, value) ->
                period to value
            }
        }

        val currentPeriods = currentRows.map { it.first }
        val mergedKnownPeriods = currentPeriods.map { period ->
            period to incomingValues[period].orEmpty()
        }

        val missingPeriods = incomingValues.keys.filterNot { it in currentPeriods }

        return mergedKnownPeriods + missingPeriods.map { period ->
            period to incomingValues[period].orEmpty()
        }
    }

    private fun persistCurrentState() {
        val state = _uiState.value

        val model = CurrentUserConditionsModel(
            powerTermPriceByPeriod = state.powerTermRows.toMap(),
            energyPriceByPeriod = state.energyConsumedRows.toMap(),
            extraServices = state.extraServices,
        )

        viewModelScope.launch {
            persistCurrentUserConditionsUseCase(model)
        }
    }

    private fun isValidDecimalInput(value: String): Boolean {
        return value.matches(Regex("^\\d*([.,]\\d{0,8})?$"))
    }
}

data class CurrentUserConditionsFormState(
    val powerTermRows: List<Pair<String, String>> = emptyList(),
    val energyConsumedRows: List<Pair<String, String>> = emptyList(),
    val extraServices: String = "",
)
