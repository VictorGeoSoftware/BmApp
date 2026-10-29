package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.domain.repository.ReportAccessRepository
import com.briel.marnisos.brielapp.domain.usecases.ForceRefreshReportAccessUseCase

fun ForceRefreshReportAccessUseCase.Factory.create(
    reportAccessRepository: ReportAccessRepository,
): ForceRefreshReportAccessUseCase = ForceRefreshReportAccessUseCase {
    reportAccessRepository.forceRefresh()
}
