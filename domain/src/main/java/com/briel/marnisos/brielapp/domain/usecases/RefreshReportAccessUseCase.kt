package com.briel.marnisos.brielapp.domain.usecases

fun interface RefreshReportAccessUseCase {
    suspend operator fun invoke()
    companion object Factory
}
