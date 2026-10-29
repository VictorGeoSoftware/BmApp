package com.briel.marnisos.brielapp.domain.usecases

import com.briel.marnisos.brielapp.domain.models.ComparatorReportPdfModel

/**
 * Requests the comprehensive PDF for an existing study. Failures are
 * [com.briel.marnisos.brielapp.domain.models.PremiumReportException].
 */
fun interface GeneratePremiumReportPdfUseCase {
    suspend operator fun invoke(
        jobId: String,
        comparison: ComparatorReportPdfModel,
        forceTokenRefresh: Boolean,
    ): Result<ByteArray>
    companion object Factory
}
