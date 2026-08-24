package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.CollectedPricesModel
import com.briel.marnisos.brielapp.domain.models.ConsumptionSessionModel
import com.briel.marnisos.brielapp.domain.models.CurrentUserConditionsModel

/**
 * Builds the collected-prices payload from the active study and the broker's entered
 * conditions.
 *
 * Returns null when there is nothing worth sending: no session, no supplier, or no
 * parseable price at all. Deciding *whether* the tariff is in scope is a separate
 * concern — see [ShouldCollectPricesUseCase].
 *
 * Only the periods belonging to the active study are included, so a 3.0TD supply
 * yields P1..P6 while a shorter tariff yields only the periods it uses.
 */
fun interface BuildCollectedPricesUseCase {

    operator fun invoke(
        session: ConsumptionSessionModel?,
        currentUserConditions: CurrentUserConditionsModel?,
    ): CollectedPricesModel?

    companion object Factory
}

fun BuildCollectedPricesUseCase.Factory.create(): BuildCollectedPricesUseCase =
    BuildCollectedPricesUseCase { session, currentUserConditions ->
        if (session == null || currentUserConditions == null) {
            return@BuildCollectedPricesUseCase null
        }

        val companyName = currentUserConditions.companyName.trim()
        if (companyName.isBlank()) return@BuildCollectedPricesUseCase null

        val powerPrices = session.powerPeriods.pricesFrom(
            currentUserConditions.powerTermPriceByPeriod,
        )
        val energyPrices = session.energyPeriods.pricesFrom(
            currentUserConditions.energyPriceByPeriod,
        )
        if (powerPrices.isEmpty() && energyPrices.isEmpty()) {
            return@BuildCollectedPricesUseCase null
        }

        CollectedPricesModel(
            companyName = companyName,
            tariffName = session.tariffName,
            powerTermPriceByPeriod = powerPrices,
            energyPriceByPeriod = energyPrices,
            extraServices = currentUserConditions.extraServices.toPriceOrNull(),
        )
    }

private fun List<String>.pricesFrom(
    pricesByPeriod: Map<String, String>,
): Map<String, Double> = mapNotNull { period ->
    pricesByPeriod[period]?.toPriceOrNull()?.let { price -> period to price }
}.toMap()

/**
 * Parses a price as typed on the form. The keyboard allows a comma as the decimal
 * separator, so it is normalised before parsing.
 */
private fun String?.toPriceOrNull(): Double? =
    this?.trim()?.replace(oldChar = ',', newChar = '.')?.toDoubleOrNull()
