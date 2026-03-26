package com.briel.marnisos.brielapp.ui.views.pricetable.export

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AndroidComparatorPdfGenerator(
    private val context: Context
) : ComparatorPdfGenerator {

    override suspend fun generate(documentData: ComparatorPdfDocumentData): Result<File> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val outputDirectory = File(context.cacheDir, SHARED_PDF_DIRECTORY).apply {
                    mkdirs()
                }
                val outputFile = File(outputDirectory, buildFileName())

                val pdfDocument = PdfDocument()
                val writer = PdfWriter(pdfDocument)

                writer.drawTitle("Comparador de propuestas")
                writer.drawSection("Datos de consumo")
                writer.drawLine("Tarifa", documentData.tariffName)

                writer.drawSubSection("Término de potencia")
                documentData.powerTermRows.forEach { (period, value) ->
                    writer.drawLine(period, value.toString())
                }

                writer.drawSubSection("Energía consumida")
                documentData.energyConsumedRows.forEach { (period, value) ->
                    writer.drawLine(period, value.toString())
                }

                writer.drawSubSection("Impuestos")
                writer.drawLine("IVA", documentData.iva)
                writer.drawLine("Impuesto eléctrico", documentData.impuestoElectrico)

                writer.drawSection("Propuestas")
                if (documentData.proposals.isEmpty()) {
                    writer.drawLine("Estado", "Sin propuestas visibles")
                } else {
                    writer.drawProposalMatrix(documentData.proposals)
                }

                writer.closeCurrentPageIfNeeded()

                FileOutputStream(outputFile).use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
                pdfDocument.close()
                outputFile
            }
        }
    }

    private fun buildFileName(): String {
        val timestamp = LocalDateTime.now().format(FILE_TIMESTAMP_FORMATTER)
        return "comparador_$timestamp.pdf"
    }

    private class PdfWriter(private val pdfDocument: PdfDocument) {
        private val titlePaint = Paint().apply {
            textSize = 20f
            isFakeBoldText = true
        }
        private val sectionPaint = Paint().apply {
            textSize = 16f
            isFakeBoldText = true
        }
        private val subSectionPaint = Paint().apply {
            textSize = 13f
            isFakeBoldText = true
        }
        private val bodyPaint = Paint().apply {
            textSize = 12f
        }
        private val matrixTitlePaint = Paint().apply {
            textSize = 10f
            isFakeBoldText = true
        }
        private val matrixBodyPaint = Paint().apply {
            textSize = 8.5f
        }
        private val matrixBorderPaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }

        private var pageNumber = 0
        private var page: PdfDocument.Page? = null
        private var yPosition = TOP_MARGIN

        fun drawTitle(text: String) {
            ensureSpace(28f)
            canvas().drawText(text, LEFT_MARGIN, yPosition, titlePaint)
            yPosition += 30f
        }

        fun drawSection(text: String) {
            ensureSpace(22f)
            canvas().drawText(text, LEFT_MARGIN, yPosition, sectionPaint)
            yPosition += 24f
        }

        fun drawSubSection(text: String) {
            ensureSpace(18f)
            canvas().drawText(text, LEFT_MARGIN, yPosition, subSectionPaint)
            yPosition += 20f
        }

        fun drawLine(label: String, value: String) {
            ensureSpace(16f)
            val text = "$label: $value"
            canvas().drawText(text, LEFT_MARGIN + 8f, yPosition, bodyPaint)
            yPosition += 16f
        }

        fun drawSpacer() {
            ensureSpace(8f)
            yPosition += 8f
        }

        fun drawProposalMatrix(
            proposals: List<ComparatorPdfProposalData>,
            columns: Int = 3
        ) {
            val cardWidth = (
                PAGE_WIDTH - LEFT_MARGIN - RIGHT_MARGIN - ((columns - 1) * MATRIX_COLUMN_GAP)
                ) / columns

            proposals.chunked(columns).forEachIndexed { rowIndex, rowProposals ->
                ensureSpace(MATRIX_CARD_HEIGHT + MATRIX_ROW_GAP)
                val rowTop = yPosition

                rowProposals.forEachIndexed { colIndex, proposal ->
                    val x = LEFT_MARGIN + colIndex * (cardWidth + MATRIX_COLUMN_GAP)
                    drawProposalCard(
                        index = rowIndex * columns + colIndex + 1,
                        proposal = proposal,
                        left = x,
                        top = rowTop,
                        width = cardWidth,
                        height = MATRIX_CARD_HEIGHT
                    )
                }

                yPosition += MATRIX_CARD_HEIGHT + MATRIX_ROW_GAP
            }
        }

        fun closeCurrentPageIfNeeded() {
            page?.let {
                pdfDocument.finishPage(it)
                page = null
            }
        }

        private fun ensureSpace(requiredHeight: Float) {
            val availableHeight = PAGE_HEIGHT - BOTTOM_MARGIN
            if (page == null) {
                startPage()
                return
            }
            if (yPosition + requiredHeight > availableHeight) {
                closeCurrentPageIfNeeded()
                startPage()
            }
        }

        private fun startPage() {
            pageNumber += 1
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            page = pdfDocument.startPage(pageInfo)
            yPosition = TOP_MARGIN
        }

        private fun drawProposalCard(
            index: Int,
            proposal: ComparatorPdfProposalData,
            left: Float,
            top: Float,
            width: Float,
            height: Float
        ) {
            val right = left + width
            val bottom = top + height
            canvas().drawRect(left, top, right, bottom, matrixBorderPaint)

            var textY = top + 12f
            val textX = left + 6f
            val maxCharsPerLine = 30

            canvas().drawText(
                "${index}. ${proposal.title}".truncate(maxCharsPerLine),
                textX,
                textY,
                matrixTitlePaint
            )
            textY += 12f

            val fixedAmountValue = proposal.fixedAmount.ifBlank { "0.00" }
            val powerSeries = proposal.powerTermItems.toSeries("P")
            val consumptionSeries = proposal.consumedEnergyItems.toSeries("P")

            val lines = listOf(
                "Potencia: $powerSeries",
                "Coste potencia: ${proposal.annualPowerTermCost}€",
                "Consumo: $consumptionSeries",
                "Coste energía: ${proposal.annualEnergyCost}€",
                "Servicios extra: $fixedAmountValue€",
                "IVA: ${proposal.iva}",
                "Imp. eléctrico: ${proposal.electricalTax}",
                "TOTAL: ${proposal.totalAnnualPrice}€"
            )

            lines.forEach { line ->
                canvas().drawText(line.truncate(maxCharsPerLine), textX, textY, matrixBodyPaint)
                textY += 10f
            }
        }

        private fun String.truncate(maxChars: Int): String {
            if (length <= maxChars) return this
            return take(maxChars - 1) + "…"
        }

        private fun List<Double>.toSeries(prefix: String): String {
            return mapIndexed { index, value ->
                "$prefix${index + 1}:$value"
            }.joinToString(" ")
        }

        private fun canvas() = checkNotNull(page).canvas

        private companion object {
            const val PAGE_WIDTH = 595
            const val PAGE_HEIGHT = 842
            const val LEFT_MARGIN = 36f
            const val RIGHT_MARGIN = 36f
            const val TOP_MARGIN = 42f
            const val BOTTOM_MARGIN = 36f
            const val MATRIX_COLUMN_GAP = 10f
            const val MATRIX_ROW_GAP = 10f
            const val MATRIX_CARD_HEIGHT = 108f
        }
    }

    private companion object {
        const val SHARED_PDF_DIRECTORY = "shared_pdfs"
        val FILE_TIMESTAMP_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
    }
}
