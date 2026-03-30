package com.briel.marnisos.brielapp.data.utils

import com.briel.marnisos.brielapp.data.model.prices.ComparatorReportColumn
import com.briel.marnisos.brielapp.data.model.prices.ComparatorReportPdfRequest
import com.briel.marnisos.brielapp.data.model.prices.ComparatorReportPeriodIntValue
import com.briel.marnisos.brielapp.data.model.prices.ComparatorReportPeriodValue
import com.briel.marnisos.brielapp.data.model.prices.ComparatorReportProposal
import com.briel.marnisos.brielapp.domain.models.ComparatorReportColumnModel
import com.briel.marnisos.brielapp.domain.models.ComparatorReportPdfModel
import com.briel.marnisos.brielapp.domain.models.ComparatorReportPeriodIntValueModel
import com.briel.marnisos.brielapp.domain.models.ComparatorReportPeriodValueModel
import com.briel.marnisos.brielapp.domain.models.ComparatorReportProposalModel

object ComparatorReportPdfMapper {

    fun ComparatorReportPdfModel.toData(): ComparatorReportPdfRequest {
        return ComparatorReportPdfRequest(
            title = title,
            supplyHolder = supplyHolder,
            supplyAddress = supplyAddress,
            cups = cups,
            tariffName = tariffName,
            powerTermRows = powerTermRows.map { it.toData() },
            energyConsumedRows = energyConsumedRows.map { it.toData() },
            iva = iva,
            impuestoElectrico = impuestoElectrico,
            customerConditions = customerConditions.toData(),
            proposals = proposals.map { it.toData() }
        )
    }

    private fun ComparatorReportPeriodValueModel.toData(): ComparatorReportPeriodValue {
        return ComparatorReportPeriodValue(period = period, value = value)
    }

    private fun ComparatorReportPeriodIntValueModel.toData(): ComparatorReportPeriodIntValue {
        return ComparatorReportPeriodIntValue(period = period, value = value)
    }

    private fun ComparatorReportColumnModel.toData(): ComparatorReportColumn {
        return ComparatorReportColumn(
            title = title,
            powerTermItems = powerTermItems,
            annualPowerTermCost = annualPowerTermCost,
            consumedEnergyItems = consumedEnergyItems,
            annualEnergyCost = annualEnergyCost,
            extraServices = extraServices,
            electricalTax = electricalTax,
            iva = iva,
            totalAnnualPrice = totalAnnualPrice
        )
    }

    private fun ComparatorReportProposalModel.toData(): ComparatorReportProposal {
        return ComparatorReportProposal(
            title = title,
            powerTermItems = powerTermItems,
            annualPowerTermCost = annualPowerTermCost,
            consumedEnergyItems = consumedEnergyItems,
            annualEnergyCost = annualEnergyCost,
            extraServices = extraServices,
            electricalTax = electricalTax,
            iva = iva,
            totalAnnualPrice = totalAnnualPrice,
            annualPriceDifference = annualPriceDifference,
            annualSavingsPercentage = annualSavingsPercentage
        )
    }
}
