package com.briel.marnisos.brielapp.ui.views.pricetable.export

import com.briel.marnisos.brielapp.domain.models.ProposalPriceModel

class ComparatorPdfDocumentDataFactory {

    fun create(
        tariffName: String,
        powerTermRows: List<Pair<String, Double>>,
        energyConsumedRows: List<Pair<String, Int>>,
        iva: String,
        impuestoElectrico: String,
        proposalPriceList: List<ProposalPriceModel>,
        proposalFixedAmountByTitle: Map<String, String>,
        proposalVisibilityByTitle: Map<String, Boolean>
    ): ComparatorPdfDocumentData {
        val visibleProposals = proposalPriceList.filter { proposal ->
            proposalVisibilityByTitle[proposal.proposalTitle] ?: true
        }

        val proposalData = visibleProposals.map { proposal ->
            ComparatorPdfProposalData(
                title = proposal.proposalTitle,
                powerTermItems = proposal.powerTermItems,
                annualPowerTermCost = proposal.annualPowerTermCostFormatted,
                consumedEnergyItems = proposal.consumedEnergyItems,
                annualEnergyCost = proposal.annualEnergyCostFormatted,
                fixedAmount = proposalFixedAmountByTitle[proposal.proposalTitle].orEmpty(),
                iva = proposal.ivaFormatted,
                electricalTax = proposal.electricalTaxFormatted,
                totalAnnualPrice = proposal.totalAnnualPriceFormatted
            )
        }

        return ComparatorPdfDocumentData(
            tariffName = tariffName,
            powerTermRows = powerTermRows,
            energyConsumedRows = energyConsumedRows,
            iva = iva,
            impuestoElectrico = impuestoElectrico,
            proposals = proposalData
        )
    }
}
