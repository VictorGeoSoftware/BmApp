package com.briel.marnisos.brielapp.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Notifies observers that backend prices changed and the active study should be refreshed.
 *
 * Replaces the previous global `PriceUpdatesEventBus` object so the dependency is
 * explicit and testable.
 */
interface PriceUpdatesNotifier {

    val events: Flow<Unit>

    fun publishPriceUpdate()
}
