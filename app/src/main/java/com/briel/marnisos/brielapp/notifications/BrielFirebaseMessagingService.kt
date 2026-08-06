package com.briel.marnisos.brielapp.notifications

import com.briel.marnisos.brielapp.domain.usecases.ClearCurrentUserConditionsUseCase
import com.briel.marnisos.brielapp.domain.usecases.ClearLastCompletedJobIdUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetCurrentAuthUserUseCase
import com.briel.marnisos.brielapp.domain.usecases.SignOutLocallyUseCase
import com.briel.marnisos.brielapp.logging.AppLogger
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BrielFirebaseMessagingService : FirebaseMessagingService(), KoinComponent {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val getCurrentAuthUserUseCase: GetCurrentAuthUserUseCase by inject()
    private val signOutLocallyUseCase: SignOutLocallyUseCase by inject()
    private val clearCurrentUserConditionsUseCase: ClearCurrentUserConditionsUseCase by inject()
    private val clearLastCompletedJobIdUseCase: ClearLastCompletedJobIdUseCase by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val maskedToken = token.take(12) + "..."
        AppLogger.i(TAG, "New FCM token received (masked): $maskedToken")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        AppLogger.d(TAG, "FCM message received from: ${message.from}")

        val notificationPayload = message.notification
        val data = message.data

        // Show system notification if notification payload is present
        if (notificationPayload != null) {
            val title = notificationPayload.title.orEmpty()
            val body = notificationPayload.body.orEmpty()
            AppLogger.d(TAG, "Notification - Title: $title, Body: $body")

            if (title.isNotBlank() && body.isNotBlank()) {
                val handler = NotificationHandler(applicationContext)
                handler.showNotification(title, body, data)
            }
        }

        // Handle data-only messages
        if (data.isNotEmpty()) {
            AppLogger.d(TAG, "Data payload: $data")
            handleDataMessage(data)
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        when (data["type"]) {
            PRICE_UPDATES_TYPE -> {
                serviceScope.launch {
                    PriceUpdatesEventBus.publishPriceUpdate()
                    AppLogger.i(TAG, "Published in-app price update event from FCM")
                }
            }
            FORCE_LOGOUT_TYPE -> handleForceLogout(data["email"])
        }
    }

    /**
     * The backend revoked this account's access and wiped its data. Sign out
     * LOCALLY only: the account's rows are already gone server-side, so any
     * authenticated call (logout, set-offline) would fail or recreate them.
     */
    private fun handleForceLogout(revokedEmail: String?) {
        val currentEmail = getCurrentAuthUserUseCase()?.email?.trim()?.lowercase()
        val targetEmail = revokedEmail?.trim()?.lowercase()

        if (currentEmail.isNullOrBlank() || targetEmail.isNullOrBlank() || currentEmail != targetEmail) {
            AppLogger.d(TAG, "Ignoring force-logout message (not for the signed-in account)")
            return
        }

        AppLogger.i(TAG, "Force-logout received for the signed-in account")
        serviceScope.launch {
            runCatching {
                clearCurrentUserConditionsUseCase()
                clearLastCompletedJobIdUseCase()
                signOutLocallyUseCase()
            }.onFailure { error ->
                AppLogger.e(TAG, "Force-logout cleanup failed", error)
            }
            ForceLogoutEventBus.publishForceLogout()
        }
    }

    companion object {
        private const val TAG = "BrielFcmService"
        private const val PRICE_UPDATES_TYPE = "price_updates"
        private const val FORCE_LOGOUT_TYPE = "force_logout"
    }
}
