package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.CollectedPricesModel

/**
 * Sends the customer's current prices to the backend for analysis.
 *
 * Fire-and-forget: the broker's flow must never be blocked or interrupted by this
 * call, so callers ignore the result beyond reporting it as a non-fatal.
 */
fun interface SubmitCollectedPricesUseCase {

    suspend operator fun invoke(collectedPrices: CollectedPricesModel): Result<Unit>

    companion object Factory
}
