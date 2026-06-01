package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.domain.usecases.SubmitConsumptionReportByCupsJobUseCase

fun SubmitConsumptionReportByCupsJobUseCase.Factory.create(
    repository: Repository,
): SubmitConsumptionReportByCupsJobUseCase = SubmitConsumptionReportByCupsJobUseCase { cupsCode ->
    repository.submitConsumptionReportJobByCups(cupsCode)
}
