package com.briel.marnisos.brielapp.data.repository

import com.briel.marnisos.brielapp.domain.repository.PriceUpdatesNotifier
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class PriceUpdatesNotifierImpl : PriceUpdatesNotifier {

    private val _events = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override val events: Flow<Unit> = _events.asSharedFlow()

    override fun publishPriceUpdate() {
        _events.tryEmit(Unit)
    }
}
