package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.ConsumptionReportModel
import java.io.File

fun interface UploadConsumptionReportUseCase {
    suspend operator fun invoke(pdfFile: File): Result<ConsumptionReportModel>
    companion object Factory
}
