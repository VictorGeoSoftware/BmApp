package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.JobSubmissionModel

fun interface SubmitConsumptionReportByCupsJobUseCase {
    suspend operator fun invoke(cupsCode: String): Result<JobSubmissionModel>
    companion object Factory
}
