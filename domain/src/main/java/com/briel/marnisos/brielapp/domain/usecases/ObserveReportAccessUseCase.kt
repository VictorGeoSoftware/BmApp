package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.ReportAccessModel
import kotlinx.coroutines.flow.Flow

fun interface ObserveReportAccessUseCase {
    operator fun invoke(): Flow<ReportAccessModel>
    companion object Factory
}
