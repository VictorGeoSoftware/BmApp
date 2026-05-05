package com.briel.marnisos.brielapp.logging

import android.util.Log
import com.briel.marnisos.brielapp.BuildConfig

object AppLogger {

    private val isLoggingEnabled: Boolean
        get() = !(BuildConfig.BUILD_TYPE == "release" && BuildConfig.FLAVOR == "prod")

    fun d(tag: String, message: String) {
        if (!isLoggingEnabled) return
        Log.d(tag, message)
    }

    fun i(tag: String, message: String) {
        if (!isLoggingEnabled) return
        Log.i(tag, message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (!isLoggingEnabled) return
        if (throwable == null) {
            Log.e(tag, message)
            return
        }

        Log.e(tag, message, throwable)
    }
}
