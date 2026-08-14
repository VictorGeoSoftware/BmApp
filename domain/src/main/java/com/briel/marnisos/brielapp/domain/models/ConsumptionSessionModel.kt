package com.briel.marnisos.brielapp.domain.models

/**
 * Single source of truth for the currently loaded consumption study.
 *
 * A session exists only after a consumption report has been fetched (by CUPS scan or
 * PDF upload). Its absence is what closes the first fee-first gate.
 */
data class ConsumptionSessionModel(
    val jobId: String,
    val tariffName: String,
    val ivaPercent: Double,
    val electricTaxPercent: Double,
    val powerTermRows: List<Pair<String, Double>>,
    val energyConsumedRows: List<Pair<String, Int>>,
    val supplyHolder: String,
    val supplyAddress: String,
    val supplyCupsCode: String,
    val proposals: List<ProposalPriceModel>,
) {
    val powerPeriods: List<String> get() = powerTermRows.map { row -> row.first }
    val energyPeriods: List<String> get() = energyConsumedRows.map { row -> row.first }
}
