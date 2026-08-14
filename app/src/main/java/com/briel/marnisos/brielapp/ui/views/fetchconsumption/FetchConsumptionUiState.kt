package com.briel.marnisos.brielapp.ui.views.fetchconsumption

import com.briel.marnisos.brielapp.domain.models.ConsumptionStudyFailure
import com.briel.marnisos.brielapp.domain.models.ConsumptionStudyStep

/**
 * State of the mandatory "fetch consumption" step of the fee-first flow.
 */
data class FetchConsumptionUiState(
    val isStudyRunning: Boolean = false,
    val step: ConsumptionStudyStep? = null,
    val failure: ConsumptionStudyFailure? = null,
)
