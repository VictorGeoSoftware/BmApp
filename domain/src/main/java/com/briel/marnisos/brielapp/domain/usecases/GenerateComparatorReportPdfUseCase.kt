package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.ComparatorReportPdfModel

fun interface GenerateComparatorReportPdfUseCase {
    suspend operator fun invoke(reportModel: ComparatorReportPdfModel): Result<ByteArray>
    companion object Factory
}
