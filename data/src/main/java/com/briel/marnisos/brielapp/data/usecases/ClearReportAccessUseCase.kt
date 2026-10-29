package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.domain.repository.ReportAccessRepository
import com.briel.marnisos.brielapp.domain.usecases.ClearReportAccessUseCase

fun ClearReportAccessUseCase.Factory.create(
    reportAccessRepository: ReportAccessRepository,
): ClearReportAccessUseCase = ClearReportAccessUseCase {
    reportAccessRepository.clear()
}
