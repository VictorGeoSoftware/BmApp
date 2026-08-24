package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.TariffCanonicalForm

/**
 * Decides whether a customer's current prices should be collected and sent to the
 * backend.
 *
 * Rule: collect for every tariff except 2.0TD, and never for an unknown (blank)
 * tariff. Comparison is done on the canonical tariff form so that spacing, casing or
 * punctuation drift in the bill read cannot let a 2.0TD supply through.
 */
fun interface ShouldCollectPricesUseCase {

    operator fun invoke(tariffName: String?): Boolean

    companion object Factory
}

fun ShouldCollectPricesUseCase.Factory.create(): ShouldCollectPricesUseCase =
    ShouldCollectPricesUseCase { tariffName ->
        val canonical = TariffCanonicalForm.of(tariffName.orEmpty())
        canonical.isNotBlank() && canonical != TariffCanonicalForm.EXCLUDED_FROM_COLLECTION
    }
