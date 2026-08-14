package com.briel.marnisos.brielapp.domain.models

/**
 * Result of evaluating the fee-first gate.
 *
 * [completedRequiredFieldCount] / [requiredFieldCount] drive the progress indicator on
 * the current conditions screen. Extra services are deliberately excluded: filling them
 * in is the broker's choice and never blocks navigation.
 */
data class FeeFirstGateModel(
    val hasFetchedConsumption: Boolean = false,
    val requiredFieldCount: Int = 0,
    val completedRequiredFieldCount: Int = 0,
) {
    val areCurrentConditionsComplete: Boolean
        get() = requiredFieldCount > 0 && completedRequiredFieldCount == requiredFieldCount

    val stage: FeeFirstStage
        get() = when {
            !hasFetchedConsumption -> FeeFirstStage.CONSUMPTION_REQUIRED
            !areCurrentConditionsComplete -> FeeFirstStage.CURRENT_CONDITIONS_REQUIRED
            else -> FeeFirstStage.UNLOCKED
        }

    val isUnlocked: Boolean get() = stage == FeeFirstStage.UNLOCKED
}
