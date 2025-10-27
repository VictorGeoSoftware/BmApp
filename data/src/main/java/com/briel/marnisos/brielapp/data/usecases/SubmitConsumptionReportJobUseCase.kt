package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.domain.usecases.SubmitConsumptionReportJobUseCase

fun SubmitConsumptionReportJobUseCase.Factory.create(
    repository: Repository
): SubmitConsumptionReportJobUseCase = SubmitConsumptionReportJobUseCase { pdfFile ->
    repository.submitConsumptionReportJob(pdfFile)
}
