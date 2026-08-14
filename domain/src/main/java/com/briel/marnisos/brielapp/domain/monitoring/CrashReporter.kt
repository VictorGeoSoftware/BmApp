package com.briel.marnisos.brielapp.domain.monitoring

interface CrashReporter {
    fun setCollectionEnabled(enabled: Boolean)

    fun setStaticAppContext(
        deployVersion: String,
        flavor: String,
        buildType: String,
        appVersion: String,
    )

    fun setScreenContext(screenName: String)

    fun setUseCaseContext(useCase: String)

    fun recordNonFatal(
        throwable: Throwable,
        category: CrashErrorCategory,
        operation: String,
    )
}
