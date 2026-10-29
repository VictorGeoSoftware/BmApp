package com.briel.marnisos.brielapp.ui.views.pricetable.export

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

class ComparatorPdfShareManager {

    fun sharePdf(
        context: Context,
        pdfFile: File,
        subject: String = DEFAULT_SUBJECT,
        chooserTitle: String = DEFAULT_CHOOSER_TITLE,
    ) {
        val chooserIntent = Intent.createChooser(buildShareIntent(context, pdfFile, subject), chooserTitle)
        if (context !is Activity) {
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(chooserIntent)
    }

    /** The ACTION_SEND intent handed to the chooser (FileProvider URI + read grant). */
    fun buildShareIntent(context: Context, pdfFile: File, subject: String = DEFAULT_SUBJECT): Intent {
        val contentUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )

        return Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private companion object {
        const val DEFAULT_SUBJECT = "Comparador de propuestas"
        const val DEFAULT_CHOOSER_TITLE = "Compartir PDF"
    }
}
