package com.briel.marnisos.brielapp.data.local

import android.content.Context
import androidx.core.content.edit
import java.util.UUID

/**
 * Provides a stable, opaque per-install device identifier (phone UUID) used to
 * bind a single phone to an account (one-phone-per-account requirement).
 *
 * The id is generated once and persisted. It is intentionally NOT cleared on
 * logout so the same install keeps its identity across sessions.
 */
class DeviceIdLocalDataSource(
    context: Context
) {
    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun getOrCreate(): String {
        sharedPreferences.getString(KEY_DEVICE_ID, null)?.let { return it }
        val newId = UUID.randomUUID().toString()
        sharedPreferences.edit { putString(KEY_DEVICE_ID, newId) }
        return newId
    }

    private companion object {
        private const val PREFERENCES_NAME = "bm_app_device_preferences"
        private const val KEY_DEVICE_ID = "device_id"
    }
}
