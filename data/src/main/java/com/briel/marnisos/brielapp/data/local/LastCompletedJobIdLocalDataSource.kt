package com.briel.marnisos.brielapp.data.local

import android.content.Context
import androidx.core.content.edit

class LastCompletedJobIdLocalDataSource(
    context: Context
) {
    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun persist(jobId: String) {
        sharedPreferences.edit {
            putString(KEY_LAST_COMPLETED_JOB_ID, jobId)
                .putLong(KEY_LAST_COMPLETED_JOB_ID_SAVED_AT, System.currentTimeMillis())
        }
    }

    fun getValidOrNull(): String? {
        val savedAt = sharedPreferences.getLong(KEY_LAST_COMPLETED_JOB_ID_SAVED_AT, 0L)
        val jobId = sharedPreferences.getString(KEY_LAST_COMPLETED_JOB_ID, null)

        if (jobId == null || savedAt == 0L) {
            clear()
            return null
        }

        val isExpired = System.currentTimeMillis() - savedAt > JOB_ID_EXPIRY_MS
        if (isExpired) {
            clear()
            return null
        }

        return jobId
    }

    fun clear() {
        sharedPreferences.edit {
            remove(KEY_LAST_COMPLETED_JOB_ID)
                .remove(KEY_LAST_COMPLETED_JOB_ID_SAVED_AT)
        }
    }

    private companion object {
        private const val PREFERENCES_NAME = "bm_app_comparator_preferences"
        private const val KEY_LAST_COMPLETED_JOB_ID = "last_completed_job_id"
        private const val KEY_LAST_COMPLETED_JOB_ID_SAVED_AT = "last_completed_job_id_saved_at"
        private const val JOB_ID_EXPIRY_MS = 60 * 60 * 1000L
    }
}
