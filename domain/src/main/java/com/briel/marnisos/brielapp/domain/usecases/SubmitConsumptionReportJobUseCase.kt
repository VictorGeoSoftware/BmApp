package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.JobSubmissionModel
import java.io.File

fun interface SubmitConsumptionReportJobUseCase {
    suspend operator fun invoke(pdfFile: File): Result<JobSubmissionModel>
    companion object Factory
}
