package com.briel.marnisos.brielapp.ui.views.pricetable

import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel
import kotlin.math.round

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

    private fun Double.roundToTwoDecimals(): Double {
        return round(this * 100.0) / 100.0
    }
}
