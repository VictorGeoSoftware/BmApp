package com.briel.marnisos.brielapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import com.briel.marnisos.brielapp.data.di.dataModule
import com.briel.marnisos.brielapp.di.appModule
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BrielApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        startKoin {
            androidContext(this@BrielApp)
            modules(listOf(dataModule, appModule))
        }

        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        initializeFcm()
    }

    private fun initializeFcm() {
        applicationScope.launch {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                Log.i(TAG, "FCM token obtained (masked): ${token.take(12)}...")

                FirebaseMessaging.getInstance()
                    .subscribeToTopic(PRICE_UPDATES_TOPIC)
                    .await()
                Log.i(TAG, "Subscribed to FCM topic: $PRICE_UPDATES_TOPIC")
            } catch (e: Exception) {
                Log.e(TAG, "FCM initialization failed", e)
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = getString(R.string.notification_channel_description)
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    companion object {
        private const val TAG = "BrielApp"
        private const val PRICE_UPDATES_TOPIC = "price_updates"
    }
}
