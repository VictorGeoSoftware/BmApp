package com.briel.marnisos.brielapp.data.utils

import java.util.Locale

object FormatUtils {
    fun List<Double>.formatEnergyDecimals(): List<Double> {
        return this.map { value ->
            String.format(Locale.getDefault(), "%.6f", value).toDouble()
        }
    }

    fun Double.formatPriceDecimals(): Double {
        return String.format(Locale.getDefault(), "%.2f", this).toDouble()
    }
}
