package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.calculations.ProposalCalculationHelper
import com.briel.marnisos.brielapp.domain.models.ComparatorSummaryModel
import com.briel.marnisos.brielapp.domain.models.ConsumptionSessionModel
import com.briel.marnisos.brielapp.domain.models.CurrentUserConditionsModel
import com.briel.marnisos.brielapp.domain.models.CustomerConditionsColumnModel
import java.util.Locale
import kotlin.math.round

/**
 * Combines the active study, the customer's current prices and the per-proposal fixed
 * amounts into everything the comparator screens render.
 *
 * This is the pricing rule of the app and therefore lives in the domain layer.
 */
fun interface CalculateComparatorSummaryUseCase {

    operator fun invoke(
        session: ConsumptionSessionModel?,
        currentUserConditions: CurrentUserConditionsModel?,
        fixedAmountByTitle: Map<String, String>,
    ): ComparatorSummaryModel

    companion object Factory
}

fun CalculateComparatorSummaryUseCase.Factory.create(
    proposalCalculationHelper: ProposalCalculationHelper,
): CalculateComparatorSummaryUseCase =
    CalculateComparatorSummaryUseCase { session, currentUserConditions, fixedAmountByTitle ->
        if (session == null) return@CalculateComparatorSummaryUseCase ComparatorSummaryModel()

        val recalculatedProposals = session.proposals.map { proposal ->
            proposalCalculationHelper.recalculateProposalWithAdditionalAmount(
                proposal = proposal,
                additionalAmount = fixedAmountByTitle[proposal.proposalTitle].parseDecimal(),
                ivaPercent = session.ivaPercent,
            )
        }

        val powerPriceByPeriod = currentUserConditions?.powerTermPriceByPeriod.orEmpty()
        val energyPriceByPeriod = currentUserConditions?.energyPriceByPeriod.orEmpty()
        val extraServicesValue = currentUserConditions?.extraServices.parseDecimal()

        val powerTermItems = session.powerTermRows.map { row ->
            powerPriceByPeriod[row.first].parseDecimal()
        }
        val consumedEnergyItems = session.energyConsumedRows.map { row ->
            energyPriceByPeriod[row.first].parseDecimal()
        }

        val annualPowerTermCost = session.powerTermRows
            .zip(powerTermItems)
            .sumOf { (powerRow, powerPrice) -> powerRow.second * powerPrice * DAYS_IN_YEAR }

        val annualEnergyCost = session.energyConsumedRows
            .zip(consumedEnergyItems)
            .sumOf { (energyRow, energyPrice) -> energyRow.second * energyPrice }

        val baseCost = annualPowerTermCost + annualEnergyCost
        val ivaAmount = baseCost * (session.ivaPercent / 100.0)
        val electricalTax = baseCost * (session.electricTaxPercent / 100.0)
        val hasCustomerPrices = currentUserConditions != null &&
            (powerTermItems.any { it > 0.0 } || consumedEnergyItems.any { it > 0.0 })
        val totalAnnualPrice = baseCost + extraServicesValue + ivaAmount + electricalTax

        val customerColumn = CustomerConditionsColumnModel(
            powerTermItems = powerTermItems,
            annualPowerTermCost = annualPowerTermCost.toTwoDecimals(),
            consumedEnergyItems = consumedEnergyItems,
            annualEnergyCost = annualEnergyCost.toTwoDecimals(),
            extraServices = extraServicesValue.toTwoDecimals(),
            electricTax = electricalTax.toTwoDecimals(),
            iva = ivaAmount.toTwoDecimals(),
            totalAnnualPrice = totalAnnualPrice.toTwoDecimals(),
        )

        val deltas = mutableMapOf<String, Double>()
        val savings = mutableMapOf<String, Int>()

        if (hasCustomerPrices) {
            recalculatedProposals.forEach { proposal ->
                deltas[proposal.proposalTitle] = proposalCalculationHelper.calculateAnnualPriceDelta(
                    customerTotalAnnualPrice = totalAnnualPrice,
                    proposalTotalAnnualPrice = proposal.totalAnnualPrice,
                )
                savings[proposal.proposalTitle] =
                    proposalCalculationHelper.calculateAnnualSavingsPercentage(
                        customerTotalAnnualPrice = totalAnnualPrice,
                        proposalTotalAnnualPrice = proposal.totalAnnualPrice,
                    )
            }
        }

        ComparatorSummaryModel(
            proposals = recalculatedProposals,
            customerConditions = if (hasCustomerPrices) customerColumn else CustomerConditionsColumnModel(),
            customerTotalAnnualPrice = if (hasCustomerPrices) totalAnnualPrice else 0.0,
            annualPriceDeltaByTitle = deltas,
            annualSavingsPercentageByTitle = savings,
        )
    }

private const val DAYS_IN_YEAR = 365.0

private fun String?.parseDecimal(): Double {
    if (this.isNullOrBlank()) return 0.0
    return this.replace(oldChar = ',', newChar = '.').toDoubleOrNull() ?: 0.0
}

private fun Double.toTwoDecimals(): String {
    val roundedValue = round(this * 100.0) / 100.0
    return String.format(Locale.US, "%.2f", roundedValue)
}
