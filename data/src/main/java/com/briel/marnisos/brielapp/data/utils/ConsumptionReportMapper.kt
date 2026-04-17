package com.briel.marnisos.brielapp.data.utils

import com.briel.marnisos.brielapp.data.model.prices.CleanedConsumptionData
import com.briel.marnisos.brielapp.data.model.prices.ConsumptionReportResponse
import com.briel.marnisos.brielapp.data.model.prices.CustomerCurrentConditionsResponse
import com.briel.marnisos.brielapp.data.model.prices.CustomerDetails
import com.briel.marnisos.brielapp.data.model.prices.CustomerId
import com.briel.marnisos.brielapp.data.model.prices.DoclingExtractedData
import com.briel.marnisos.brielapp.data.model.prices.EnergyTermConditionResponse
import com.briel.marnisos.brielapp.data.model.prices.ExtraServiceConditionResponse
import com.briel.marnisos.brielapp.data.model.prices.PowerTermConditionResponse
import com.briel.marnisos.brielapp.data.model.prices.ProposalPriceData
import com.briel.marnisos.brielapp.data.utils.FormatUtils.formatEnergyDecimals
import com.briel.marnisos.brielapp.data.utils.FormatUtils.formatPriceDecimals
import com.briel.marnisos.brielapp.domain.models.CleanedConsumptionDataModel
import com.briel.marnisos.brielapp.domain.models.ConsumptionReportModel
import com.briel.marnisos.brielapp.domain.models.CustomerCurrentConditionsModel
import com.briel.marnisos.brielapp.domain.models.CustomerDetailsModel
import com.briel.marnisos.brielapp.domain.models.CustomerIdModel
import com.briel.marnisos.brielapp.domain.models.DoclingExtractedDataModel
import com.briel.marnisos.brielapp.domain.models.EnergyTermConditionModel
import com.briel.marnisos.brielapp.domain.models.ExtraServiceConditionModel
import com.briel.marnisos.brielapp.domain.models.PowerTermConditionModel
import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel

object ConsumptionReportMapper {
    fun ConsumptionReportResponse.mapToDomain(): ConsumptionReportModel {
        return ConsumptionReportModel(
            success = this.success,
            userData = this.userData.mapToDomain(),
            currentConditions = this.currentConditions?.mapToDomain(),
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

    private fun CustomerCurrentConditionsResponse.mapToDomain(): CustomerCurrentConditionsModel {
        return CustomerCurrentConditionsModel(
            cups = this.cups,
            atrTariff = this.atr_tariff,
            contractedPowerPuntaKw = this.contracted_power_punta_kw,
            contractedPowerValleKw = this.contracted_power_valle_kw,
            powerTerms = this.power_terms.map { it.mapToDomain() },
            energyTerms = this.energy_terms.map { it.mapToDomain() },
            extraServices = this.extra_services.map { it.mapToDomain() },
            ivaPercentage = this.iva_percentage,
            electricalTaxPercentage = this.electrical_tax_percentage,
            billingPeriodDays = this.billing_period_days,
            totalAmount = this.total_amount,
            loyaltyDiscount = this.loyalty_discount,
        )
    }

    private fun PowerTermConditionResponse.mapToDomain(): PowerTermConditionModel {
        return PowerTermConditionModel(
            period = this.period,
            contractedKw = this.contracted_kw,
            pricePerKwDay = this.price_per_kw_day,
            billedAmount = this.billed_amount,
            periodDescription = this.period_description,
        )
    }

    private fun EnergyTermConditionResponse.mapToDomain(): EnergyTermConditionModel {
        return EnergyTermConditionModel(
            period = this.period,
            pricePerKwh = this.price_per_kwh,
            consumedKwh = this.consumed_kwh,
            billedAmount = this.billed_amount,
            periodDescription = this.period_description,
        )
    }

    private fun ExtraServiceConditionResponse.mapToDomain(): ExtraServiceConditionModel {
        return ExtraServiceConditionModel(
            name = this.name,
            unitPrice = this.unit_price,
            unitType = this.unit_type,
            quantity = this.quantity,
            totalBilled = this.total_billed,
            isMeterRental = this.is_meter_rental,
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
