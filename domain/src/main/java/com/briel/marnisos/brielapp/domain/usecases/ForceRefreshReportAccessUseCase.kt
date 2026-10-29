package com.briel.marnisos.brielapp.domain.usecases

/** Hides Premium actions at once and re-checks access, bypassing the throttle. */
fun interface ForceRefreshReportAccessUseCase {
    suspend operator fun invoke()
    companion object Factory
}
