package com.briel.marnisos.brielapp.ui.views.pricetable.export

import android.content.Intent
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.briel.marnisos.brielapp.R
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/** Spec scenario 1: stored informe_completo PDF shared through the existing FileProvider. */
@RunWith(AndroidJUnit4::class)
class ComprehensiveReportShareIntentTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val pdf = "%PDF-1.4 test".toByteArray()

    @Test
    fun premium_pdf_is_stored_in_shared_pdfs_and_shared_as_a_readable_content_uri() {
        val file = ComparatorPdfFileStore(context).store(pdf, "informe_completo").getOrThrow()
        assertEquals("shared_pdfs", file.parentFile?.name)
        assertTrue(file.name, Regex("""informe_completo_\d{8}_\d{6}\.pdf""").matches(file.name))

        val subject = context.getString(R.string.proposals_comprehensive_report)
        val intent = ComparatorPdfShareManager().buildShareIntent(context, file, subject)

        assertEquals(Intent.ACTION_SEND, intent.action)
        assertEquals("application/pdf", intent.type)
        assertEquals(subject, intent.getStringExtra(Intent.EXTRA_SUBJECT))
        assertTrue(intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION != 0)
        @Suppress("DEPRECATION")
        val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)!!
        assertEquals("content", uri.scheme)
        assertEquals("${context.packageName}.fileprovider", uri.authority)
        val read = context.contentResolver.openInputStream(uri)!!.use { it.readBytes() }
        assertArrayEquals(pdf, read)
        file.delete()
    }

    @Test
    fun basic_defaults_are_unchanged() {
        val file = ComparatorPdfFileStore(context).store(pdf).getOrThrow()
        assertTrue(file.name, Regex("""comparador_\d{8}_\d{6}\.pdf""").matches(file.name))
        val intent = ComparatorPdfShareManager().buildShareIntent(context, file)
        assertEquals("Comparador de propuestas", intent.getStringExtra(Intent.EXTRA_SUBJECT))
        file.delete()
    }

    @Test
    fun opening_the_chooser_launches_the_system_share_sheet() {
        val file = ComparatorPdfFileStore(context).store(pdf, "informe_completo").getOrThrow()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val monitor = instrumentation.addMonitor(
            android.content.IntentFilter(Intent.ACTION_CHOOSER), null, true,
        )
        ComparatorPdfShareManager().sharePdf(
            context = context,
            pdfFile = file,
            subject = context.getString(R.string.proposals_comprehensive_report),
            chooserTitle = context.getString(R.string.premium_report_share_chooser),
        )
        instrumentation.waitForMonitorWithTimeout(monitor, 5_000)
        assertEquals(1, monitor.hits)
        instrumentation.removeMonitor(monitor)
        file.delete()
    }
}
