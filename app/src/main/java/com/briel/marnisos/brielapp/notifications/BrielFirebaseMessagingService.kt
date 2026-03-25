package com.briel.marnisos.brielapp.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BrielFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val maskedToken = token.take(12) + "..."
        Log.i(TAG, "New FCM token received (masked): $maskedToken")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "FCM message received from: ${message.from}")

        val notificationPayload = message.notification
        val data = message.data

        // Show system notification if notification payload is present
        if (notificationPayload != null) {
            val title = notificationPayload.title.orEmpty()
            val body = notificationPayload.body.orEmpty()
            Log.d(TAG, "Notification - Title: $title, Body: $body")

            if (title.isNotBlank() && body.isNotBlank()) {
                val handler = NotificationHandler(applicationContext)
                handler.showNotification(title, body, data)
            }
        }

        // Handle data-only messages
        if (data.isNotEmpty()) {
            Log.d(TAG, "Data payload: $data")
            handleDataMessage(data)
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val dataType = data["type"]
        if (dataType == PRICE_UPDATES_TYPE) {
            serviceScope.launch {
                PriceUpdatesEventBus.publishPriceUpdate()
                Log.i(TAG, "Published in-app price update event from FCM")
            }
        }
    }

    companion object {
        private const val TAG = "BrielFcmService"
        private const val PRICE_UPDATES_TYPE = "price_updates"
    }
}
