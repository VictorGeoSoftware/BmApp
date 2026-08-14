package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.domain.repository.ConsumptionSessionRepository
import com.briel.marnisos.brielapp.domain.usecases.EvaluateFeeFirstGateUseCase
import com.briel.marnisos.brielapp.domain.usecases.ObserveFeeFirstGateUseCase
import kotlinx.coroutines.flow.combine

fun ObserveFeeFirstGateUseCase.Factory.create(
    repository: Repository,
    consumptionSessionRepository: ConsumptionSessionRepository,
    evaluateFeeFirstGateUseCase: EvaluateFeeFirstGateUseCase,
): ObserveFeeFirstGateUseCase = ObserveFeeFirstGateUseCase {
    combine(
        consumptionSessionRepository.session,
        repository.observeCurrentUserConditions(),
    ) { session, currentUserConditions ->
        evaluateFeeFirstGateUseCase(session, currentUserConditions)
    }
}
