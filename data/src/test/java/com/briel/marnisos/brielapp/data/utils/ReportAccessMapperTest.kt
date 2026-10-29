package com.briel.marnisos.brielapp.data.utils

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/** Truth table rows T1–T5: capability-only, total parser, fail-hidden. */
class ReportAccessMapperTest {

    @Test
    fun `T1 capability present with PREMIUM tier is allowed`() {
        assertTrue(ReportAccessMapper.canRequestComprehensiveReport("""{"tier":"PREMIUM","capabilities":["COMPREHENSIVE_COMPARATOR_PDF"]}"""))
    }

    @Test
    fun `T1 capability present with BASIC tier is allowed (tier ignored)`() {
        assertTrue(ReportAccessMapper.canRequestComprehensiveReport("""{"tier":"BASIC","capabilities":["COMPREHENSIVE_COMPARATOR_PDF"]}"""))
    }

    @Test
    fun `T2 basic with empty capabilities is not allowed`() {
        assertFalse(ReportAccessMapper.canRequestComprehensiveReport("""{"tier":"BASIC","capabilities":[]}"""))
    }

    @Test
    fun `T3 premium tier without the capability is not allowed`() {
        assertFalse(ReportAccessMapper.canRequestComprehensiveReport("""{"tier":"PREMIUM","capabilities":[]}"""))
    }

    @Test
    fun `T4 capability among unknown and non-string items is allowed`() {
        assertTrue(ReportAccessMapper.canRequestComprehensiveReport("""{"tier":"X","capabilities":[1,null,{"a":1},["COMPREHENSIVE_COMPARATOR_PDF"],"OTHER","COMPREHENSIVE_COMPARATOR_PDF"]}"""))
    }

    @Test
    fun `T4 non-string lookalikes alone are not allowed`() {
        assertFalse(ReportAccessMapper.canRequestComprehensiveReport("""{"capabilities":[["COMPREHENSIVE_COMPARATOR_PDF"],{"c":"COMPREHENSIVE_COMPARATOR_PDF"}]}"""))
    }

    @Test
    fun `exact string match only`() {
        assertFalse(ReportAccessMapper.canRequestComprehensiveReport("""{"capabilities":["comprehensive_comparator_pdf"," COMPREHENSIVE_COMPARATOR_PDF"]}"""))
    }

    @Test
    fun `T5 malformed shapes are not allowed`() {
        listOf(
            """{"tier":"PREMIUM"}""",
            """{"tier":"PREMIUM","capabilities":null}""",
            """{"capabilities":"COMPREHENSIVE_COMPARATOR_PDF"}""",
            """{"capabilities":{"0":"COMPREHENSIVE_COMPARATOR_PDF"}}""",
            """["COMPREHENSIVE_COMPARATOR_PDF"]""",
            "\"COMPREHENSIVE_COMPARATOR_PDF\"",
            "null",
            "",
            "not json",
            "<html>COMPREHENSIVE_COMPARATOR_PDF</html>",
            """{"capabilities":["COMPREHENSIVE_COMPARATOR_PDF"""",
        ).forEach { body ->
            assertFalse(ReportAccessMapper.canRequestComprehensiveReport(body), body)
        }
    }
}
