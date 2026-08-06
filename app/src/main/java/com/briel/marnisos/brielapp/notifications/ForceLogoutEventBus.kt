package com.briel.marnisos.brielapp.notifications

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/**
 * In-app event published after the FCM force-logout handler has signed the
 * user out locally, so the UI can navigate to the login screen and explain
 * that the access was revoked.
 */
object ForceLogoutEventBus {
    private val _events = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val events: SharedFlow<Unit> = _events

    fun publishForceLogout() {
        _events.tryEmit(Unit)
    }
}
