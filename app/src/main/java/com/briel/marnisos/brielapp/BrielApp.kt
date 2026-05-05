package com.briel.marnisos.brielapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.briel.marnisos.brielapp.data.di.dataModule
import com.briel.marnisos.brielapp.di.appModule
import com.briel.marnisos.brielapp.logging.AppLogger
import com.briel.marnisos.brielapp.monitoring.CrashReporter
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.android.getKoin
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

        configureCrashMonitoring()

        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        initializeFcm()
    }

    private fun configureCrashMonitoring() {
        val crashReporter = getKoin().get<CrashReporter>()
        val shouldCollectCrashes = BuildConfig.BUILD_TYPE == "release" && BuildConfig.FLAVOR == "prod"

        crashReporter.setCollectionEnabled(shouldCollectCrashes)
        crashReporter.setStaticAppContext(
            deployVersion = BuildConfig.DEPLOY_VERSION,
            flavor = BuildConfig.FLAVOR,
            buildType = BuildConfig.BUILD_TYPE,
            appVersion = BuildConfig.VERSION_NAME,
        )
    }

    private fun initializeFcm() {
        applicationScope.launch {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                AppLogger.i(TAG, "FCM token obtained (masked): ${token.take(12)}...")

                FirebaseMessaging.getInstance()
                    .subscribeToTopic(PRICE_UPDATES_TOPIC)
                    .await()
                AppLogger.i(TAG, "Subscribed to FCM topic: $PRICE_UPDATES_TOPIC")
            } catch (e: Exception) {
                AppLogger.e(TAG, "FCM initialization failed", e)
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
