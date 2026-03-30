package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.domain.usecases.GenerateComparatorReportPdfUseCase

fun GenerateComparatorReportPdfUseCase.Factory.create(
    repository: Repository
): GenerateComparatorReportPdfUseCase = GenerateComparatorReportPdfUseCase { reportModel ->
    repository.generateComparatorReportPdf(reportModel)
}
