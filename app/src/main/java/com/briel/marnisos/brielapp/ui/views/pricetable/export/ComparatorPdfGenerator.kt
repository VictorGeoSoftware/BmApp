package com.briel.marnisos.brielapp.ui.views.pricetable.export

import java.io.File

interface ComparatorPdfGenerator {
    suspend fun generate(documentData: ComparatorPdfDocumentData): Result<File>
}
