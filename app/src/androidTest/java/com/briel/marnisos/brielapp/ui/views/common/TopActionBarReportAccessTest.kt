package com.briel.marnisos.brielapp.ui.views.common

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.briel.marnisos.brielapp.R
import com.briel.marnisos.brielapp.ui.theme.BrielAppTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TopActionBarReportAccessTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val full get() = rule.activity.getString(R.string.proposals_comprehensive_report)
    private val create get() = rule.activity.getString(R.string.proposals_create_report)
    private var printClicks = 0
    private var fullClicks = 0

    private fun show(print: Boolean, comprehensive: Boolean) {
        rule.setContent {
            BrielAppTheme(darkTheme = false) {
                Box(Modifier.width(360.dp)) {
                    TopActionBar(
                        isGeneratingPdf = false,
                        showPrintButton = print,
                        onGeneratePdfClick = { printClicks++ },
                        onOpenDrawer = {},
                        showComprehensiveReportButton = comprehensive,
                        onComprehensiveReportClick = { fullClicks++ },
                    )
                }
            }
        }
    }

    @Test
    fun t1_premium_shows_enabled_full_report_right_of_create_report_at_360dp() {
        show(print = true, comprehensive = true)
        val createNode = rule.onNodeWithText(create).assertIsDisplayed().assertIsEnabled()
        val fullNode = rule.onNodeWithText(full).assertIsDisplayed().assertIsEnabled()
        val createBounds = createNode.fetchSemanticsNode().boundsInRoot
        val fullBounds = fullNode.fetchSemanticsNode().boundsInRoot
        assertTrue("to the right without overlap", fullBounds.left >= createBounds.right)
        assertTrue("fits in 360dp", fullBounds.right <= with(rule.density) { 360.dp.toPx() } + 0.5f)
        saveScreenshot("t1-premium-360dp.png")
    }

    /** Visual evidence only; written to the app's external files dir (pulled with adb when needed). */
    private fun saveScreenshot(name: String) {
        val dir = rule.activity.getExternalFilesDir(null) ?: return
        java.io.File(dir, name).outputStream().use { out ->
            rule.onRoot().captureToImage().asAndroidBitmap()
                .compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
        }
    }

    @Test
    fun t15_tapping_full_report_requests_it_and_not_the_basic_report() {
        // report-android-export enabled the action (it was disabled in report-android-capability).
        show(print = true, comprehensive = true)
        rule.onNodeWithText(full).performClick()
        assertEquals(1, fullClicks)
        assertEquals(0, printClicks)
    }

    @Test
    fun t2_basic_shows_only_create_report() {
        show(print = true, comprehensive = false)
        rule.onNodeWithText(create).assertIsDisplayed().assertIsEnabled()
        rule.onNodeWithText(full).assertDoesNotExist()
        saveScreenshot("t2-basic-360dp.png")
    }

    @Test
    fun t14_no_proposals_or_other_route_hides_both() {
        show(print = false, comprehensive = false)
        rule.onNodeWithText(create).assertDoesNotExist()
        rule.onNodeWithText(full).assertDoesNotExist()
    }
}
