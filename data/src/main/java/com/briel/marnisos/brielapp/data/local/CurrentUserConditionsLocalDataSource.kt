package com.briel.marnisos.brielapp.data.local

import android.content.Context
import androidx.core.content.edit
import com.briel.marnisos.brielapp.domain.models.CurrentUserConditionsModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CurrentUserConditionsLocalDataSource(
    context: Context,
) {
    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val currentConditionsFlow = MutableStateFlow(readCurrentConditions())

    fun observe(): StateFlow<CurrentUserConditionsModel?> = currentConditionsFlow

    fun persist(currentUserConditions: CurrentUserConditionsModel) {
        sharedPreferences.edit {
            putString(KEY_POWER_TERM_PRICE_BY_PERIOD, serializeMap(currentUserConditions.powerTermPriceByPeriod))
            putString(KEY_ENERGY_PRICE_BY_PERIOD, serializeMap(currentUserConditions.energyPriceByPeriod))
            putString(KEY_EXTRA_SERVICES, currentUserConditions.extraServices)
        }

        currentConditionsFlow.value = currentUserConditions
    }

    fun clearData() {
        sharedPreferences.edit {
            remove(KEY_POWER_TERM_PRICE_BY_PERIOD)
            remove(KEY_ENERGY_PRICE_BY_PERIOD)
            remove(KEY_EXTRA_SERVICES)
        }

        currentConditionsFlow.value = null
    }

    private fun readCurrentConditions(): CurrentUserConditionsModel? {
        val powerMap = deserializeMap(sharedPreferences.getString(KEY_POWER_TERM_PRICE_BY_PERIOD, null))
        val energyMap = deserializeMap(sharedPreferences.getString(KEY_ENERGY_PRICE_BY_PERIOD, null))
        val extraServices = sharedPreferences.getString(KEY_EXTRA_SERVICES, "").orEmpty()

        if (powerMap.isEmpty() && energyMap.isEmpty() && extraServices.isBlank()) {
            return null
        }

        return CurrentUserConditionsModel(
            powerTermPriceByPeriod = powerMap,
            energyPriceByPeriod = energyMap,
            extraServices = extraServices,
        )
    }

    private fun serializeMap(values: Map<String, String>): String {
        return values.entries.joinToString(separator = ENTRY_SEPARATOR) { (key, value) ->
            "$key$KEY_VALUE_SEPARATOR$value"
        }
    }

    private fun deserializeMap(serializedMap: String?): Map<String, String> {
        if (serializedMap.isNullOrBlank()) return emptyMap()

        return serializedMap
            .split(ENTRY_SEPARATOR)
            .mapNotNull { entry ->
                val separatorIndex = entry.indexOf(KEY_VALUE_SEPARATOR)
                if (separatorIndex <= 0) return@mapNotNull null

                val key = entry.substring(startIndex = 0, endIndex = separatorIndex)
                val value = entry.substring(separatorIndex + KEY_VALUE_SEPARATOR.length)
                if (key.isBlank()) return@mapNotNull null

                key to value
            }
            .toMap()
    }

    private companion object {
        private const val PREFERENCES_NAME = "bm_app_comparator_preferences"
        private const val KEY_POWER_TERM_PRICE_BY_PERIOD = "power_term_price_by_period"
        private const val KEY_ENERGY_PRICE_BY_PERIOD = "energy_price_by_period"
        private const val KEY_EXTRA_SERVICES = "extra_services"
        private const val ENTRY_SEPARATOR = "|"
        private const val KEY_VALUE_SEPARATOR = "="
    }
}
