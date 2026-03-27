package com.briel.marnisos.brielapp.ui.views.comparator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.briel.marnisos.brielapp.ui.components.tables.SideTitleTableView
import com.briel.marnisos.brielapp.ui.theme.Corner
import com.briel.marnisos.brielapp.ui.theme.extendedColors
import com.briel.marnisos.brielapp.ui.views.common.HeaderBox
import com.briel.marnisos.brielapp.ui.views.common.SectionHeader
import com.briel.marnisos.brielapp.ui.views.common.SimpleTwoColumnHeader
import com.briel.marnisos.brielapp.ui.views.common.SimpleTwoColumnTable

@Composable
internal fun UserConsumptionDataView(
    modifier: Modifier = Modifier,
    tariffName: String,
    powerTermRows: List<Pair<String, Double>>,
    energyConsumedRows: List<Pair<String, Int>>,
    iva: String,
    impuestoElectrico: String,
) {
    val colors = extendedColors

    Column(
        modifier
            .width(400.dp)
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TableHeader(tariffName = tariffName)

        // Power term table
        SideTitleTableView(
            sideTitle = "TÉRMINO DE\nPOTENCIA",
            leftValues = powerTermRows.map { it.first },
            rightValues = powerTermRows.map { it.second.toString() },
        )

        SectionHeader(
            modifier = Modifier.fillMaxWidth(),
            text = "Coste anual termino potencia",
            background = colors.headerHighlight,
            corner = Corner
        )

        // Energy consumed table
        SideTitleTableView(
            sideTitle = "ENERGÍA\nCONSUMIDA",
            leftValues = energyConsumedRows.map { it.first },
            rightValues = energyConsumedRows.map { it.second.toString() },
        )

        SectionHeader(
            modifier = Modifier.fillMaxWidth(),
            text = "Coste anual termino energía",
            background = colors.headerHighlight,
            corner = Corner
        )

        // Extra services table (2 columns)
        SimpleTwoColumnHeader(
            modifier = Modifier.height(57.dp),
            leftHeader = "SERVICIOS EXTRA",
            rightHeader = "Coste Anual",
        )
        SimpleTwoColumnTable(
            iva = iva,
            impuestoElectrico = impuestoElectrico,
        )

        // Final total banner
        HeaderBox(
            modifier = Modifier.fillMaxWidth(),
            text = "COSTE ANUAL FACTURA ELÉCTRICA",
            background = colors.headerHighlight,
            corner = Corner
        )
    }
}


@Composable
private fun TableHeader(
    tariffName: String
) {
    val colors = extendedColors

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HeaderBox(
            modifier = Modifier.weight(1.0f),
            text = tariffName,
            background = colors.headerHighlight,
            corner = Corner
        )
        HeaderBox(
            modifier = Modifier.weight(1.0f),
            text = "CONSUMO ANUAL",
            background = colors.headerHighlight,
            corner = Corner
        )
    }
}
