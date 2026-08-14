package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.FeeFirstGateModel
import kotlinx.coroutines.flow.Flow

/**
 * Observes the fee-first gate, recomputing whenever the active study or the
 * customer's current conditions change.
 */
fun interface ObserveFeeFirstGateUseCase {

    operator fun invoke(): Flow<FeeFirstGateModel>

    companion object Factory
}
