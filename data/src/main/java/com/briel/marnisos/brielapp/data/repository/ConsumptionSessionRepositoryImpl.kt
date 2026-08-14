package com.briel.marnisos.brielapp.data.repository

import com.briel.marnisos.brielapp.domain.models.ConsumptionReportModel
import com.briel.marnisos.brielapp.domain.models.ConsumptionSessionModel
import com.briel.marnisos.brielapp.domain.repository.ConsumptionSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * In-memory implementation of the consumption session single source of truth.
 *
 * The session is deliberately not persisted: it is rebuilt on start from the last
 * completed job id, which is what [com.briel.marnisos.brielapp.data.local.LastCompletedJobIdLocalDataSource]
 * stores.
 */
internal class ConsumptionSessionRepositoryImpl : ConsumptionSessionRepository {

    /** Guards the automatic hiding so it runs once per study + customer-price combination. */
    private var lastAutoHideSignature: String? = null

    private val _session = MutableStateFlow<ConsumptionSessionModel?>(value = null)
    override val session: StateFlow<ConsumptionSessionModel?> = _session.asStateFlow()

    private val _proposalVisibilityByTitle = MutableStateFlow<Map<String, Boolean>>(value = emptyMap())
    override val proposalVisibilityByTitle: StateFlow<Map<String, Boolean>> =
        _proposalVisibilityByTitle.asStateFlow()

    private val _proposalFixedAmountByTitle = MutableStateFlow<Map<String, String>>(value = emptyMap())
    override val proposalFixedAmountByTitle: StateFlow<Map<String, String>> =
        _proposalFixedAmountByTitle.asStateFlow()

    private var isSupplyHolderOverriddenByUser: Boolean = false
    private var lastBackendSupplyHolder: String = ""

    override fun updateFromReport(jobId: String, report: ConsumptionReportModel) {
        val backendSupplyHolder = report.userData.customerDetails?.name.orEmpty()
        val currentSupplyHolder = _session.value?.supplyHolder.orEmpty()
        val shouldApplyBackendSupplyHolder = !isSupplyHolderOverriddenByUser &&
            (currentSupplyHolder.isBlank() || currentSupplyHolder == lastBackendSupplyHolder)

        val resolvedSupplyHolder = if (shouldApplyBackendSupplyHolder) {
            backendSupplyHolder
        } else {
            currentSupplyHolder
        }

        lastBackendSupplyHolder = backendSupplyHolder

        _session.value = ConsumptionSessionModel(
            jobId = jobId,
            tariffName = report.consumptionData.feeType,
            ivaPercent = report.iva,
            electricTaxPercent = report.impuestoElectrico,
            powerTermRows = report.consumptionData.subscribedPowerValues(),
            energyConsumedRows = report.consumptionData.annualConsumptionValues()
                .map { item -> item.first to item.second.toInt() },
            supplyHolder = resolvedSupplyHolder,
            supplyAddress = _session.value?.supplyAddress
                ?: report.userData.customerDetails?.address.orEmpty(),
            supplyCupsCode = report.userData.cupsCode.ifBlank { report.consumptionData.cups },
            proposals = report.proposals,
        )

        synchronizeProposalOverrides(report.proposals)
    }

    override fun updateSupplyHolder(supplyHolder: String) {
        isSupplyHolderOverriddenByUser = true
        _session.update { current -> current?.copy(supplyHolder = supplyHolder) }
    }

    override fun updateSupplyAddress(supplyAddress: String) {
        _session.update { current -> current?.copy(supplyAddress = supplyAddress) }
    }

    override fun setProposalVisibility(proposalTitle: String, isVisible: Boolean) {
        _proposalVisibilityByTitle.update { current ->
            if (!current.containsKey(proposalTitle)) return@update current
            current + (proposalTitle to isVisible)
        }
    }

    override fun setProposalFixedAmount(proposalTitle: String, fixedAmountInput: String) {
        _proposalFixedAmountByTitle.update { current ->
            if (!current.containsKey(proposalTitle)) return@update current
            current + (proposalTitle to fixedAmountInput)
        }
    }

    override fun hideProposalsOnce(signature: String, proposalTitles: Set<String>) {
        if (signature == lastAutoHideSignature) return
        lastAutoHideSignature = signature

        if (proposalTitles.isEmpty()) return

        _proposalVisibilityByTitle.update { current ->
            // Only touch titles the study actually returned, mirroring setProposalVisibility.
            current + proposalTitles
                .filter { title -> current.containsKey(title) }
                .associateWith { false }
        }
    }

    override fun clear() {
        _session.value = null
        _proposalVisibilityByTitle.value = emptyMap()
        _proposalFixedAmountByTitle.value = emptyMap()
        isSupplyHolderOverriddenByUser = false
        lastBackendSupplyHolder = ""
        lastAutoHideSignature = null
    }

    private fun synchronizeProposalOverrides(
        proposals: List<com.briel.marnisos.brielapp.domain.models.ProposalPriceModel>,
    ) {
        val currentVisibility = _proposalVisibilityByTitle.value
        _proposalVisibilityByTitle.value = proposals.associate { proposal ->
            proposal.proposalTitle to (currentVisibility[proposal.proposalTitle] ?: true)
        }

        val currentFixedAmounts = _proposalFixedAmountByTitle.value
        _proposalFixedAmountByTitle.value = proposals.associate { proposal ->
            proposal.proposalTitle to currentFixedAmounts[proposal.proposalTitle].orEmpty()
        }
    }
}
