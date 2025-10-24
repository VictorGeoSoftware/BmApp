package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.domain.usecases.UploadConsumptionReportUseCase

fun UploadConsumptionReportUseCase.Factory.create(
    repository: Repository
): UploadConsumptionReportUseCase = UploadConsumptionReportUseCase { pdfFile ->
    repository.uploadConsumptionReport(pdfFile)
}
