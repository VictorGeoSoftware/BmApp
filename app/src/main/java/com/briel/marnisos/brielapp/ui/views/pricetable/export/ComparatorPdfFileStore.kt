package com.briel.marnisos.brielapp.ui.views.pricetable.export

import android.content.Context
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ComparatorPdfFileStore(
    private val context: Context
) {

    fun store(pdfBytes: ByteArray): Result<File> {
        return runCatching {
            val outputDirectory = File(context.cacheDir, SHARED_PDF_DIRECTORY).apply {
                mkdirs()
            }
            val outputFile = File(outputDirectory, buildFileName())
            outputFile.writeBytes(pdfBytes)
            outputFile
        }
    }

    private fun buildFileName(): String {
        val timestamp = LocalDateTime.now().format(FILE_TIMESTAMP_FORMATTER)
        return "comparador_$timestamp.pdf"
    }

    private companion object {
        const val SHARED_PDF_DIRECTORY = "shared_pdfs"
        val FILE_TIMESTAMP_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
    }
}
