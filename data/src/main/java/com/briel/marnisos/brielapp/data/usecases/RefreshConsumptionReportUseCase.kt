package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.domain.usecases.RefreshConsumptionReportUseCase

fun RefreshConsumptionReportUseCase.Factory.create(
    repository: Repository
): RefreshConsumptionReportUseCase = RefreshConsumptionReportUseCase { jobId ->
    repository.refreshConsumptionReport(jobId)
}
