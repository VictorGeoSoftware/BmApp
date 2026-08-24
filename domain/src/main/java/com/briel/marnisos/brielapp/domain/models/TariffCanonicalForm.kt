package com.briel.marnisos.brielapp.domain.models

/**
 * Canonical form of a tariff name, used for comparisons.
 *
 * Tariff names reach the app as free text from the bill read (Docling/n8n), so the
 * same tariff can arrive as "3.0TD", "3.0 TD" or "3.0td ". Comparing canonical forms
 * means those variants cannot change a decision.
 *
 * The backend applies the identical rule in `CollectedPriceNormalizer`; the two must
 * stay in agreement, otherwise the 2.0TD exclusion would be enforced inconsistently.
 */
object TariffCanonicalForm {

    /** Canonical form of the tariff excluded from price collection. */
    const val EXCLUDED_FROM_COLLECTION = "20TD"

    /** Uppercases and drops every non-alphanumeric character: "2.0 TD" -> "20TD". */
    fun of(raw: String): String =
        raw.uppercase().filter { character -> character.isLetterOrDigit() }
}
