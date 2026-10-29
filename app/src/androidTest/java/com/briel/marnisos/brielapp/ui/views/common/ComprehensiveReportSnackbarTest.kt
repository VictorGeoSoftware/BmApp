package com.briel.marnisos.brielapp.ui.views.common

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.briel.marnisos.brielapp.R
import com.briel.marnisos.brielapp.ui.theme.BrielAppTheme
import com.briel.marnisos.brielapp.ui.views.proposals.ComprehensiveReportMessage
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

/** Q2 (b): outcomes shown as snackbars; Retry only where the truth table allows. */
@RunWith(AndroidJUnit4::class)
class ComprehensiveReportSnackbarTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var retries = 0
    private var dismissals = 0
    private var message by mutableStateOf<ComprehensiveReportMessage?>(null)

    private fun show(initial: ComprehensiveReportMessage?, generating: Boolean = false, locale: Locale? = null) {
        message = initial
        rule.setContent {
            val base = LocalContext.current
            val baseConfiguration = LocalConfiguration.current
            val configuration = remember(locale) {
                Configuration(baseConfiguration).apply { if (locale != null) setLocale(locale) }
            }
            val context = remember(locale) {
                if (locale == null) base else base.createConfigurationContext(configuration)
            }
            CompositionLocalProvider(LocalContext provides context, LocalConfiguration provides configuration) {
                BrielAppTheme(darkTheme = false) {
                    val host = remember { SnackbarHostState() }
                    ComprehensiveReportSnackbarEffect(
                        message = message,
                        snackbarHostState = host,
                        onRetry = { retries++; message = null },
                        onDismiss = { dismissals++; message = null },
                    )
                    Box(Modifier.width(360.dp).height(400.dp)) {
                        Column(Modifier.fillMaxWidth()) {
                            TopActionBar(
                                isGeneratingPdf = false,
                                showPrintButton = true,
                                onGeneratePdfClick = {},
                                onOpenDrawer = {},
                                showComprehensiveReportButton = true,
                                isGeneratingComprehensiveReport = generating,
                            )
                        }
                        SnackbarHost(host, Modifier.align(Alignment.BottomCenter))
                    }
                }
            }
        }
    }

    private fun str(id: Int) = rule.activity.getString(id)

    @Test
    fun s8_with_retry_calls_retry() {
        show(ComprehensiveReportMessage(R.string.premium_report_generation_failed, canRetry = true))
        rule.onNodeWithText(str(R.string.premium_report_generation_failed)).assertIsDisplayed()
        saveScreenshot("snackbar-s8-retry-en-360dp.png")
        rule.onNodeWithText(str(R.string.premium_report_retry)).performClick()
        rule.waitForIdle()
        assertEquals(1, retries)
        assertEquals(0, dismissals)
    }

    @Test
    fun s1_without_retry_has_no_action() {
        show(ComprehensiveReportMessage(R.string.premium_report_required, canRetry = false))
        rule.onNodeWithText(str(R.string.premium_report_required)).assertIsDisplayed()
        rule.onNodeWithText(str(R.string.premium_report_retry)).assertDoesNotExist()
        saveScreenshot("snackbar-s1-no-action-en-360dp.png")
    }

    @Test
    fun s2_without_retry_times_out_and_dismisses() {
        rule.mainClock.autoAdvance = true
        show(ComprehensiveReportMessage(R.string.premium_report_study_expired, canRetry = false))
        rule.onNodeWithText(str(R.string.premium_report_study_expired)).assertIsDisplayed()
        rule.waitUntil(timeoutMillis = 15_000) { dismissals == 1 }
        assertEquals(0, retries)
    }

    @Test
    fun loading_disables_the_full_report_button() {
        show(null, generating = true)
        rule.onNodeWithText(str(R.string.proposals_comprehensive_report)).assertDoesNotExist()
        saveScreenshot("loading-en-360dp.png")
    }

    @Test
    fun spanish_s8_with_reintentar() {
        show(ComprehensiveReportMessage(R.string.premium_report_generation_failed, canRetry = true), locale = Locale("es"))
        rule.onNodeWithText("No se ha podido generar el informe completo. Inténtalo de nuevo.").assertIsDisplayed()
        rule.onNodeWithText("Reintentar").assertIsDisplayed()
        saveScreenshot("snackbar-s8-retry-es-360dp.png")
    }

    @Test
    fun spanish_s1_no_action() {
        show(ComprehensiveReportMessage(R.string.premium_report_required, canRetry = false), locale = Locale("es"))
        rule.onNodeWithText("El informe completo requiere el plan Premium.").assertIsDisplayed()
        rule.onNodeWithText("Reintentar").assertDoesNotExist()
        saveScreenshot("snackbar-s1-no-action-es-360dp.png")
    }

    /** Visual evidence only; written to the app's external files dir (pulled with adb). */
    private fun saveScreenshot(name: String) {
        rule.waitForIdle()
        val dir = rule.activity.getExternalFilesDir(null) ?: return
        java.io.File(dir, name).outputStream().use { out ->
            rule.onRoot().captureToImage().asAndroidBitmap()
                .compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
        }
    }
}
