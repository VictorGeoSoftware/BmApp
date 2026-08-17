package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.ConsumptionSessionModel
import com.briel.marnisos.brielapp.domain.models.CurrentUserConditionsModel
import com.briel.marnisos.brielapp.domain.models.FeeFirstGateModel

/**
 * Evaluates the fee-first gate for a given session and set of current conditions.
 *
 * Rule: the customer's current supplier must be named, and every power-term period
 * and every energy period present in the active study must hold a valid decimal
 * greater than zero. Extra services are excluded on purpose.
 */
fun interface EvaluateFeeFirstGateUseCase {

    operator fun invoke(
        session: ConsumptionSessionModel?,
        currentUserConditions: CurrentUserConditionsModel?,
    ): FeeFirstGateModel

    companion object Factory
}

fun EvaluateFeeFirstGateUseCase.Factory.create(): EvaluateFeeFirstGateUseCase =
    EvaluateFeeFirstGateUseCase { session, currentUserConditions ->
        if (session == null) return@EvaluateFeeFirstGateUseCase FeeFirstGateModel()

        val requiredPeriodValues =
            session.powerPeriods.map { period ->
                currentUserConditions?.powerTermPriceByPeriod?.get(period)
            } + session.energyPeriods.map { period ->
                currentUserConditions?.energyPriceByPeriod?.get(period)
            }

        // The supplier counts as one more required field, so the screen's progress
        // indicator stays truthful rather than showing "complete" while it is blank.
        val isCompanyNameFilled = !currentUserConditions?.companyName.isNullOrBlank()

        FeeFirstGateModel(
            hasFetchedConsumption = true,
            requiredFieldCount = requiredPeriodValues.size + 1,
            completedRequiredFieldCount = requiredPeriodValues.count { value ->
                value.isFilledPrice()
            } + if (isCompanyNameFilled) 1 else 0,
        )
    }

private fun String?.isFilledPrice(): Boolean {
    val normalized = this?.trim()?.replace(oldChar = ',', newChar = '.').orEmpty()
    if (normalized.isBlank()) return false
    val parsed = normalized.toDoubleOrNull() ?: return false
    return parsed > 0.0
}
