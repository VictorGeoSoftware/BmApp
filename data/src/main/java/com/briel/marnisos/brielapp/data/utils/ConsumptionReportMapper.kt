package com.briel.marnisos.brielapp.data.utils

import com.briel.marnisos.brielapp.data.model.prices.CleanedConsumptionData
import com.briel.marnisos.brielapp.data.model.prices.ConsumptionReportResponse
import com.briel.marnisos.brielapp.data.model.prices.CustomerDetails
import com.briel.marnisos.brielapp.data.model.prices.CustomerId
import com.briel.marnisos.brielapp.data.model.prices.DoclingExtractedData
import com.briel.marnisos.brielapp.data.model.prices.ProposalPriceData
import com.briel.marnisos.brielapp.data.utils.FormatUtils.formatEnergyDecimals
import com.briel.marnisos.brielapp.data.utils.FormatUtils.formatPriceDecimals
import com.briel.marnisos.brielapp.domain.models.CleanedConsumptionDataModel
import com.briel.marnisos.brielapp.domain.models.ConsumptionReportModel
import com.briel.marnisos.brielapp.domain.models.CustomerDetailsModel
import com.briel.marnisos.brielapp.domain.models.CustomerIdModel
import com.briel.marnisos.brielapp.domain.models.DoclingExtractedDataModel
import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel

object ConsumptionReportMapper {
    fun ConsumptionReportResponse.mapToDomain(): ConsumptionReportModel {
        return ConsumptionReportModel(
            success = this.success,
            userData = this.userData.mapToDomain(),
            consumptionData = this.consumptionData.mapToDomain(),
            proposals = this.proposals.map { it.mapToDomain() },
            iva = this.iva,
            impuestoElectrico = this.impuestoElectrico
        )
    }

    private fun DoclingExtractedData.mapToDomain(): DoclingExtractedDataModel {
        return DoclingExtractedDataModel(
            cupsCode = this.cups_code,
            customerDetails = this.customer_details?.mapToDomain(),
            customerId = this.customer_id?.mapToDomain()
        )
    }

    private fun CustomerDetails.mapToDomain(): CustomerDetailsModel {
        return CustomerDetailsModel(
            address = this.address,
            name = this.name
        )
    }

    private fun CustomerId.mapToDomain(): CustomerIdModel {
        return CustomerIdModel(
            contextText = this.context_text,
            originalFormat = this.original_format,
            type = this.type,
            value = this.value
        )
    }

    private fun CleanedConsumptionData.mapToDomain(): CleanedConsumptionDataModel {
        return CleanedConsumptionDataModel(
            cups = this.cups,
            tarifa = this.tarifa,
            tarifaValue = this.tarifaValue,
            annualConsumption = this.annualConsumption,
            annualConsumptionP1 = this.annualConsumptionP1,
            annualConsumptionP2 = this.annualConsumptionP2,
            annualConsumptionP3 = this.annualConsumptionP3,
            annualConsumptionP4 = this.annualConsumptionP4,
            annualConsumptionP5 = this.annualConsumptionP5,
            annualConsumptionP6 = this.annualConsumptionP6,
            subscribedPowerP1 = this.subscribedPowerP1,
            subscribedPowerP2 = this.subscribedPowerP2,
            subscribedPowerP3 = this.subscribedPowerP3,
            subscribedPowerP4 = this.subscribedPowerP4,
            subscribedPowerP5 = this.subscribedPowerP5,
            subscribedPowerP6 = this.subscribedPowerP6,
            feeType = this.feeType,
            fileName = this.fileName,
            processedAt = this.processedAt
        )
    }

    private fun ProposalPriceData.mapToDomain(): ProposalPriceModel {
        return ProposalPriceModel(
            proposalTitle = this.proposalTitle,
            powerTermItems = this.powerTermItems.formatEnergyDecimals(),
            annualPowerTermCost = this.annualPowerTermCost.formatPriceDecimals(),
            consumedEnergyItems = this.consumedEnergyItems.formatEnergyDecimals(),
            annualEnergyCost = this.annualEnergyCost.formatPriceDecimals(),
            extraServices = this.extraServices,
            iva = this.iva,
            electricalTax = this.electricalTax.formatPriceDecimals(),
            totalAnnualPrice = this.totalAnnualPrice.formatPriceDecimals()
        )
    }
}
