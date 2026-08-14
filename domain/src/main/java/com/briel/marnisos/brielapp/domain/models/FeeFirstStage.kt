package com.briel.marnisos.brielapp.domain.models

/**
 * Stages of the fee-first flow. Each stage gates the next one.
 */
enum class FeeFirstStage {
    /** No consumption study loaded yet. Only CUPS scan / PDF upload is offered. */
    CONSUMPTION_REQUIRED,

    /** Consumption loaded, but the customer's current prices are still incomplete. */
    CURRENT_CONDITIONS_REQUIRED,

    /** Everything required is filled in: proposals and configuration are reachable. */
    UNLOCKED,
}
