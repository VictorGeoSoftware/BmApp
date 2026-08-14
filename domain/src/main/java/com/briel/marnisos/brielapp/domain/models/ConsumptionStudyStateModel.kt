package com.briel.marnisos.brielapp.domain.models

/**
 * Progress of the background consumption study (PDF upload or CUPS lookup).
 *
 * Owned by the data layer and application-scoped, so navigating away from the
 * fetch screen does not cancel an in-flight study.
 */
sealed interface ConsumptionStudyStateModel {

    data object Idle : ConsumptionStudyStateModel

    data class InProgress(val step: ConsumptionStudyStep) : ConsumptionStudyStateModel

    data class Failed(val cause: ConsumptionStudyFailure) : ConsumptionStudyStateModel

    val isRunning: Boolean get() = this is InProgress
}
