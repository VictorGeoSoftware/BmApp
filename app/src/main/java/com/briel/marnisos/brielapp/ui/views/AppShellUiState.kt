package com.briel.marnisos.brielapp.ui.views

import com.briel.marnisos.brielapp.domain.models.FeeFirstStage

/**
 * State of the app shell: what the fee-first gate currently allows, and whether a
 * consumption study is running.
 */
data class AppShellUiState(
    val stage: FeeFirstStage = FeeFirstStage.CONSUMPTION_REQUIRED,
    val isStudyRunning: Boolean = false,
)
