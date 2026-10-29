package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.domain.repository.ReportAccessRepository
import com.briel.marnisos.brielapp.domain.usecases.ObserveReportAccessUseCase

fun ObserveReportAccessUseCase.Factory.create(
    reportAccessRepository: ReportAccessRepository,
): ObserveReportAccessUseCase = ObserveReportAccessUseCase {
    reportAccessRepository.access
}
