package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.domain.repository.AuthRepository
import com.briel.marnisos.brielapp.domain.repository.ReportAccessRepository
import com.briel.marnisos.brielapp.domain.usecases.LogoutUseCase

fun LogoutUseCase.Factory.create(
    authRepository: AuthRepository,
    reportAccessRepository: ReportAccessRepository,
): LogoutUseCase = LogoutUseCase {
    reportAccessRepository.clear()
    authRepository.logout()
}
