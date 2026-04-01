package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.repository.Repository
import com.briel.marnisos.brielapp.domain.usecases.IncrementProposalResponseCounterUseCase

fun IncrementProposalResponseCounterUseCase.Factory.create(
    repository: Repository
): IncrementProposalResponseCounterUseCase = IncrementProposalResponseCounterUseCase { name, email ->
    repository.incrementProposalResponseCounter(name = name, email = email)
}
