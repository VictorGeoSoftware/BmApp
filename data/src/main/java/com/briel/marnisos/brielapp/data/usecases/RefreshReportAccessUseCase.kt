package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.domain.repository.ReportAccessRepository
import com.briel.marnisos.brielapp.domain.usecases.RefreshReportAccessUseCase

fun RefreshReportAccessUseCase.Factory.create(
    reportAccessRepository: ReportAccessRepository,
): RefreshReportAccessUseCase = RefreshReportAccessUseCase {
    reportAccessRepository.refresh()
}
