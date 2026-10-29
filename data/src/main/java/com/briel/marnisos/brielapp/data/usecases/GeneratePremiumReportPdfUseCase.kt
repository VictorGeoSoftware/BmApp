package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.model.prices.PremiumReportPdfRequest
import com.briel.marnisos.brielapp.data.network.PremiumReportApi
import com.briel.marnisos.brielapp.data.utils.ComparatorReportPdfMapper.toData
import com.briel.marnisos.brielapp.domain.models.PremiumReportException
import com.briel.marnisos.brielapp.domain.models.PremiumReportFailure
import com.briel.marnisos.brielapp.domain.repository.AuthRepository
import com.briel.marnisos.brielapp.domain.usecases.GeneratePremiumReportPdfUseCase

fun GeneratePremiumReportPdfUseCase.Factory.create(
    authRepository: AuthRepository,
    premiumReportApi: PremiumReportApi,
): GeneratePremiumReportPdfUseCase = GeneratePremiumReportPdfUseCase { jobId, comparison, forceTokenRefresh ->
    val token = authRepository.getIdToken(forceTokenRefresh).getOrNull()
    if (token.isNullOrBlank()) {
        Result.failure(PremiumReportException(PremiumReportFailure.UNAUTHENTICATED))
    } else {
        premiumReportApi.generate(token, PremiumReportPdfRequest(jobId = jobId, comparison = comparison.toData()))
    }
}
