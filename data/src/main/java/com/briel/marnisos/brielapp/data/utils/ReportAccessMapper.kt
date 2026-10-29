package com.briel.marnisos.brielapp.data.utils

import com.briel.marnisos.brielapp.domain.models.COMPREHENSIVE_COMPARATOR_PDF_CAPABILITY
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.Json

/**
 * Total parser for the `/me/access` body. True only when the root is an object whose
 * `capabilities` is an array containing the exact string capability. Tier is ignored.
 */
object ReportAccessMapper {
    fun canRequestComprehensiveReport(body: String): Boolean = try {
        val root = Json.parseToJsonElement(body) as? JsonObject
        val capabilities = root?.get("capabilities") as? JsonArray
        capabilities?.any { item ->
            item is JsonPrimitive && item.isString && item.content == COMPREHENSIVE_COMPARATOR_PDF_CAPABILITY
        } ?: false
    } catch (e: Exception) {
        false
    }
}
