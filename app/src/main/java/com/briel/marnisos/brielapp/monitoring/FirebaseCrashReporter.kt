package com.briel.marnisos.brielapp.monitoring

import com.google.firebase.crashlytics.FirebaseCrashlytics

class FirebaseCrashReporter(
    private val crashlytics: FirebaseCrashlytics,
) : CrashReporter {

    override fun setCollectionEnabled(enabled: Boolean) {
        crashlytics.setCrashlyticsCollectionEnabled(enabled)
    }

    override fun setStaticAppContext(
        deployVersion: String,
        flavor: String,
        buildType: String,
        appVersion: String,
    ) {
        crashlytics.setCustomKey("deploy_version", deployVersion)
        crashlytics.setCustomKey("flavor", flavor)
        crashlytics.setCustomKey("build_type", buildType)
        crashlytics.setCustomKey("app_version", appVersion)
    }

    override fun setScreenContext(screenName: String) {
        crashlytics.setCustomKey("screen_name", screenName)
    }

    override fun setUseCaseContext(useCase: String) {
        crashlytics.setCustomKey("use_case", useCase)
    }

    override fun recordNonFatal(
        throwable: Throwable,
        category: CrashErrorCategory,
        operation: String,
    ) {
        crashlytics.setCustomKey("error_category", category.name)
        crashlytics.setCustomKey("operation", operation)
        crashlytics.recordException(throwable)
    }
}
