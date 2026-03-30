package com.briel.marnisos.brielapp.ui.views.pricetable

import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel
import kotlin.math.round
import kotlin.math.roundToInt

class ProposalCalculationHelper {

    fun recalculateProposalWithAdditionalAmount(
        proposal: ProposalPriceModel,
        additionalAmount: Double,
        ivaPercent: Double
    ): ProposalPriceModel {
        if (additionalAmount <= 0.0) return proposal

        val additionalIvaPart = additionalAmount * (ivaPercent / 100.0)

        return proposal.copy(
            iva = (proposal.iva + additionalIvaPart).roundToTwoDecimals(),
            totalAnnualPrice = (proposal.totalAnnualPrice + additionalAmount).roundToTwoDecimals()
        )
    }

    fun calculateAnnualPriceDelta(
        customerTotalAnnualPrice: Double,
        proposalTotalAnnualPrice: Double,
    ): Double {
        return (customerTotalAnnualPrice - proposalTotalAnnualPrice).roundToTwoDecimals()
    }

    fun calculateAnnualSavingsPercentage(
        customerTotalAnnualPrice: Double,
        proposalTotalAnnualPrice: Double,
    ): Int {
        if (customerTotalAnnualPrice <= 0.0) return 0

        val annualPriceDelta = customerTotalAnnualPrice - proposalTotalAnnualPrice
        val savingsPercentage = (annualPriceDelta / customerTotalAnnualPrice) * 100.0
        return savingsPercentage.roundToInt()
    }

    private fun Double.roundToTwoDecimals(): Double {
        return round(this * 100.0) / 100.0
    }
}
