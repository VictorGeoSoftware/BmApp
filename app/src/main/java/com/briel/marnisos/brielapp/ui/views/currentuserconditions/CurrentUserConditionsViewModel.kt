package com.briel.marnisos.brielapp.ui.views.currentuserconditions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.briel.marnisos.brielapp.domain.models.CurrentUserConditionsModel
import com.briel.marnisos.brielapp.domain.repository.ConsumptionSessionRepository
import com.briel.marnisos.brielapp.domain.usecases.BuildCollectedPricesUseCase
import com.briel.marnisos.brielapp.domain.usecases.ObserveCurrentUserConditionsUseCase
import com.briel.marnisos.brielapp.domain.usecases.ObserveFeeFirstGateUseCase
import com.briel.marnisos.brielapp.domain.usecases.PersistCurrentUserConditionsUseCase
import com.briel.marnisos.brielapp.domain.usecases.ShouldCollectPricesUseCase
import com.briel.marnisos.brielapp.domain.usecases.SubmitCollectedPricesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Screen ViewModel for the fee-first gate.
 *
 * The persisted conditions are the single source of truth: every edit is written
 * through the repository and flows back into the UI, so no local form copy is kept.
 */
class CurrentUserConditionsViewModel(
    private val consumptionSessionRepository: ConsumptionSessionRepository,
    private val observeCurrentUserConditionsUseCase: ObserveCurrentUserConditionsUseCase,
    private val persistCurrentUserConditionsUseCase: PersistCurrentUserConditionsUseCase,
    private val shouldCollectPricesUseCase: ShouldCollectPricesUseCase,
    private val buildCollectedPricesUseCase: BuildCollectedPricesUseCase,
    private val submitCollectedPricesUseCase: SubmitCollectedPricesUseCase,
    observeFeeFirstGateUseCase: ObserveFeeFirstGateUseCase,
) : ViewModel() {

    private val latestConditions = MutableStateFlow<CurrentUserConditionsModel?>(value = null)

    /**
     * Job ids whose prices have already been submitted, so navigating back and forth
     * cannot store the same customer twice. Without a CUPS on the stored row there is
     * no other way to detect a duplicate.
     */
    private val submittedJobIds = mutableSetOf<String>()

    val uiState: StateFlow<CurrentUserConditionsUiState> = combine(
        consumptionSessionRepository.session,
        observeCurrentUserConditionsUseCase(),
        observeFeeFirstGateUseCase(),
    ) { session, conditions, gate ->
        latestConditions.value = conditions

        CurrentUserConditionsUiState(
            gate = gate,
            form = CurrentUserConditionsFormState(
                powerTermRows = session?.powerPeriods.orEmpty().map { period ->
                    period to conditions?.powerTermPriceByPeriod?.get(period).orEmpty()
                },
                energyConsumedRows = session?.energyPeriods.orEmpty().map { period ->
                    period to conditions?.energyPriceByPeriod?.get(period).orEmpty()
                },
                extraServices = conditions?.extraServices.orEmpty(),
                companyName = conditions?.companyName.orEmpty(),
            ),
            supplyHolder = session?.supplyHolder.orEmpty(),
            supplyAddress = session?.supplyAddress.orEmpty(),
            supplyCupsCode = session?.supplyCupsCode.orEmpty(),
            availableProposals = session?.proposals.orEmpty(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = CurrentUserConditionsUiState(),
    )

    fun onPowerTermValueChanged(period: String, value: String) {
        if (!isValidDecimalInput(value)) return
        persist { current ->
            current.copy(powerTermPriceByPeriod = current.powerTermPriceByPeriod + (period to value))
        }
    }

    fun onEnergyValueChanged(period: String, value: String) {
        if (!isValidDecimalInput(value)) return
        persist { current ->
            current.copy(energyPriceByPeriod = current.energyPriceByPeriod + (period to value))
        }
    }

    fun onExtraServicesChanged(value: String) {
        if (!isValidDecimalInput(value)) return
        persist { current -> current.copy(extraServices = value) }
    }

    fun onCompanyNameChanged(value: String) {
        persist { current -> current.copy(companyName = value) }
    }

    /**
     * Submits the customer's current prices for analysis, then lets navigation
     * proceed regardless of the outcome.
     *
     * Deliberately fire-and-forget: collecting prices is a background concern and must
     * never block, slow down or fail the broker's flow. A failed send is lost, which is
     * an accepted trade-off while there is no offline outbox.
     */
    fun onNavigateToProposalsClicked() {
        val session = consumptionSessionRepository.session.value ?: return
        if (!session.jobId.let(submittedJobIds::add)) return
        if (!shouldCollectPricesUseCase(session.tariffName)) return

        val collectedPrices = buildCollectedPricesUseCase(session, latestConditions.value)
            ?: return

        viewModelScope.launch {
            submitCollectedPricesUseCase(collectedPrices)
        }
    }

    fun onSupplyHolderChanged(value: String) {
        consumptionSessionRepository.updateSupplyHolder(value)
    }

    fun onSupplyAddressChanged(value: String) {
        consumptionSessionRepository.updateSupplyAddress(value)
    }

    /** Copies the selected proposal's prices into the customer's current conditions. */
    fun copyPricesFromProposal(proposalTitle: String) {
        val session = consumptionSessionRepository.session.value ?: return
        val proposal = session.proposals
            .firstOrNull { candidate -> candidate.proposalTitle == proposalTitle }
            ?: return

        val powerTermPriceByPeriod = session.powerTermRows
            .mapIndexed { index, row ->
                row.first to proposal.powerTermItems.getOrNull(index).toDecimalInput()
            }
            .toMap()

        val energyPriceByPeriod = session.energyConsumedRows
            .mapIndexed { index, row ->
                row.first to proposal.consumedEnergyItems.getOrNull(index).toDecimalInput()
            }
            .toMap()

        viewModelScope.launch {
            persistCurrentUserConditionsUseCase(
                CurrentUserConditionsModel(
                    companyName = latestConditions.value?.companyName.orEmpty(),
                    powerTermPriceByPeriod = powerTermPriceByPeriod,
                    energyPriceByPeriod = energyPriceByPeriod,
                    extraServices = String.format(Locale.US, "%.2f", proposal.extraServices),
                ),
            )
        }
    }

    private fun persist(transform: (CurrentUserConditionsModel) -> CurrentUserConditionsModel) {
        val current = latestConditions.value ?: EMPTY_CONDITIONS
        val updated = transform(current)
        latestConditions.value = updated

        viewModelScope.launch {
            persistCurrentUserConditionsUseCase(updated)
        }
    }

    private fun isValidDecimalInput(value: String): Boolean =
        value.matches(Regex("^\\d*([.,]\\d{0,8})?$"))

    private fun Double?.toDecimalInput(): String {
        if (this == null) return ""
        return String.format(Locale.US, "%.8f", this)
            .trimEnd('0')
            .trimEnd('.')
            .ifBlank { "0" }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
        val EMPTY_CONDITIONS = CurrentUserConditionsModel(
            companyName = "",
            powerTermPriceByPeriod = emptyMap(),
            energyPriceByPeriod = emptyMap(),
            extraServices = "",
        )
    }
}

/**
 * Editable rows of the current-conditions form.
 */
data class CurrentUserConditionsFormState(
    val powerTermRows: List<Pair<String, String>> = emptyList(),
    val energyConsumedRows: List<Pair<String, String>> = emptyList(),
    val extraServices: String = "",
    /** The customer's current supplier, typed by the broker. */
    val companyName: String = "",
)
