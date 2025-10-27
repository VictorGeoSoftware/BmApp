package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.ConsumptionReportModel

fun interface GetJobResultUseCase {
    suspend operator fun invoke(jobId: String): Result<ConsumptionReportModel>
    companion object Factory
}
