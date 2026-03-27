package com.briel.marnisos.brielapp.ui.views.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.briel.marnisos.brielapp.ui.components.tables.DynamicTableColumnView


@Composable
internal fun SimpleTwoColumnHeader(
    modifier: Modifier = Modifier,
    leftHeader: String,
    rightHeader: String,
) {
    Row(
        modifier = modifier.then(Modifier.fillMaxWidth()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DynamicTableColumnView(
            modifier = Modifier.weight(1.0f),
            values = listOf(leftHeader),
        )
        DynamicTableColumnView(
            modifier = Modifier.weight(1.0f),
            values = listOf(rightHeader),
        )
    }
}

@Composable
internal fun SimpleTwoColumnTable(
    iva: String,
    impuestoElectrico: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DynamicTableColumnView(
            modifier = Modifier.weight(1.0f),
            values = listOf("IMPUESTO ELÉCTRICO", "IVA"),
        )
        DynamicTableColumnView(
            modifier = Modifier.weight(1.0f),
            values = listOf(impuestoElectrico, iva),
        )
    }
}