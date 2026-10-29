package com.briel.marnisos.brielapp.data.utils

import com.briel.marnisos.brielapp.domain.models.PremiumReportFailure
import com.briel.marnisos.brielapp.domain.models.PremiumReportFailure.ACCESS_REVOKED
import com.briel.marnisos.brielapp.domain.models.PremiumReportFailure.ADDRESS_MISSING
import com.briel.marnisos.brielapp.domain.models.PremiumReportFailure.CONDITIONS_MISSING
import com.briel.marnisos.brielapp.domain.models.PremiumReportFailure.DATA_MISMATCH
import com.briel.marnisos.brielapp.domain.models.PremiumReportFailure.GENERATION_FAILED
import com.briel.marnisos.brielapp.domain.models.PremiumReportFailure.HOLDER_MISSING
import com.briel.marnisos.brielapp.domain.models.PremiumReportFailure.PREMIUM_REQUIRED
import com.briel.marnisos.brielapp.domain.models.PremiumReportFailure.STUDY_NOT_FOUND
import com.briel.marnisos.brielapp.domain.models.PremiumReportFailure.STUDY_NOT_READY
import com.briel.marnisos.brielapp.domain.models.PremiumReportFailure.UNAUTHENTICATED
import com.briel.marnisos.brielapp.domain.models.PremiumReportFailure.UNAVAILABLE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PremiumReportFailureMapperTest {

    private fun map(status: Int, code: String?) = PremiumReportFailureMapper.fromResponse(status, code)

    @Test
    fun `A7 A8 A9 auth statuses`() {
        assertEquals(PREMIUM_REQUIRED, map(403, "premium_required"))
        assertEquals(ACCESS_REVOKED, map(403, null))
        assertEquals(ACCESS_REVOKED, map(403, "something_else"))
        assertEquals(UNAUTHENTICATED, map(401, null))
        assertEquals(UNAUTHENTICATED, map(401, "premium_required"))
    }

    @Test
    fun `A10 A11 A16 study and availability codes`() {
        assertEquals(STUDY_NOT_FOUND, map(404, "study_not_found"))
        assertEquals(STUDY_NOT_READY, map(409, "study_not_ready"))
        assertEquals(UNAVAILABLE, map(501, "report_unavailable"))
    }

    @Test
    fun `A12 A13 identity and conditions codes`() {
        assertEquals(HOLDER_MISSING, map(422, "holder_missing"))
        assertEquals(ADDRESS_MISSING, map(422, "address_missing"))
        assertEquals(CONDITIONS_MISSING, map(422, "current_conditions_missing"))
    }

    @Test
    fun `A14 A15 data mismatch codes, unknown 422 code and invalid_request`() {
        listOf(
            "stale_offer", "unknown_offer", "duplicate_offer", "no_offers",
            "cups_mismatch", "tariff_mismatch", "consumption_mismatch", "tax_mismatch",
            "invalid_amount", "brand_new_code",
        ).forEach { code -> assertEquals(DATA_MISMATCH, map(422, code), code) }
        assertEquals(DATA_MISMATCH, map(400, "invalid_request"))
    }

    @Test
    fun `A17 everything else is a generation failure`() {
        assertEquals(GENERATION_FAILED, map(500, "report_generation_failed"))
        assertEquals(GENERATION_FAILED, map(500, null))
        assertEquals(GENERATION_FAILED, map(406, null))
        assertEquals(GENERATION_FAILED, map(422, null))
        assertEquals(GENERATION_FAILED, map(404, null))
        assertEquals(GENERATION_FAILED, map(409, null))
        assertEquals(GENERATION_FAILED, map(501, null))
        assertEquals(GENERATION_FAILED, map(400, null))
        assertEquals(GENERATION_FAILED, map(502, "study_not_found"))
        assertEquals(GENERATION_FAILED, map(302, null))
    }

    @Test
    fun `every failure is reachable`() {
        val reached = setOf(
            map(403, "premium_required"), map(403, null), map(401, null), map(404, "study_not_found"),
            map(409, "study_not_ready"), map(422, "holder_missing"), map(422, "address_missing"),
            map(422, "current_conditions_missing"), map(422, "stale_offer"), map(501, "report_unavailable"),
            map(500, null),
        )
        assertEquals(PremiumReportFailure.entries.toSet(), reached)
    }
}
