package com.briel.marnisos.brielapp.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ComparatorView — A Compose-only UI that mirrors the provided mockup.
 * Focused on the View layer; can be fed with a UI model from upper layers later.
 */
@Composable
fun ComparatorView(
    modifier: Modifier = Modifier,
    tariffName: String,
    annualConsumptionTitle: String,
    powerTermRows: List<Pair<String, String>>, // e.g., listOf("P1" to "5.50 kW", "P2" to "5.50 kW")
    energyConsumedRows: List<Pair<String, String>>, // e.g., listOf("P1" to "438.00 kWh", ...)
    extraServices: List<Pair<String, String>>, // e.g., listOf("IMPUESTO ELÉCTRICO" to "5.11%", "IVA" to "21%")
    totalsTitle: String,
) {
    val headerYellow = Color(0xFFF3D34A)
    val highlightBlue = Color(0xFFD0E7FF)
    val borderColor = Color(0xFF222222)
    val corner = 8.dp

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top header row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HeaderBox(
                text = tariffName,
                background = headerYellow,
                modifier = Modifier.weight(1f),
                corner = corner
            )
            HeaderBox(
                text = annualConsumptionTitle,
                background = headerYellow,
                modifier = Modifier.weight(1f),
                corner = corner
            )
        }

        // Power term table
        LabeledRowsTable(
            label = "TÉRMINO DE\nPOTENCIA",
            rows = powerTermRows,
            valueHighlight = highlightBlue,
            borderColor = borderColor,
            corner = corner
        )

        SectionHeader(text = "Coste anual termino potencia", background = headerYellow, corner = corner)

        // Energy consumed table
        LabeledRowsTable(
            label = "ENERGÍA\nCONSUMIDA",
            rows = energyConsumedRows,
            valueHighlight = null,
            borderColor = borderColor,
            corner = corner
        )

        SectionHeader(text = "Coste anual termino energía", background = headerYellow, corner = corner)

        // Extra services table (2 columns)
        SimpleTwoColumnTable(
            leftHeader = "SERVICIOS EXTRA",
            rightHeader = "Coste Anual",
            rows = extraServices,
            borderColor = borderColor,
            corner = corner
        )

        // Final total banner
        HeaderBox(
            text = totalsTitle,
            background = headerYellow,
            modifier = Modifier.fillMaxWidth(),
            corner = corner
        )
    }
}

@Composable
private fun HeaderBox(
    text: String,
    background: Color,
    modifier: Modifier = Modifier,
    corner: Dp = 8.dp
) {
    Box(
        modifier
            .background(background, RoundedCornerShape(corner))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(corner))
            .padding(vertical = 14.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 0.3.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SectionHeader(
    text: String,
    background: Color,
    modifier: Modifier = Modifier,
    corner: Dp = 8.dp
) {
    Box(
        modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(corner))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(corner))
            .padding(vertical = 10.dp, horizontal = 12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
private fun LabeledRowsTable(
    label: String,
    rows: List<Pair<String, String>>,
    valueHighlight: Color? = null,
    borderColor: Color = Color.Black,
    corner: Dp = 8.dp
) {
    val shape = RoundedCornerShape(corner)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, shape)
            .padding(0.dp)
    ) {
        // Left merged label column
        Box(
            modifier = Modifier
                .widthIn(min = 140.dp)
                .fillMaxHeight()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center
            )
        }

        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
            color = borderColor
        )

        Column(modifier = Modifier.weight(1f)) {
            rows.forEachIndexed { index, (left, right) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (valueHighlight != null && index == 0) Modifier.background(valueHighlight)
                            else Modifier
                        )
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = left,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = right,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (index != rows.lastIndex) {
                    Divider(color = borderColor.copy(alpha = 0.8f), thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
private fun SimpleTwoColumnTable(
    leftHeader: String,
    rightHeader: String,
    rows: List<Pair<String, String>>,
    borderColor: Color = Color.Black,
    corner: Dp = 8.dp
) {
    val shape = RoundedCornerShape(corner)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, shape)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF8F8F8), shape)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                leftHeader,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                rightHeader,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                textAlign = TextAlign.End
            )
        }

        Divider(color = borderColor.copy(alpha = 0.8f), thickness = 1.dp)

        rows.forEachIndexed { index, (l, r) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(l, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                Text(r, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End)
            }
            if (index != rows.lastIndex) {
                Divider(color = borderColor.copy(alpha = 0.6f), thickness = 1.dp)
            }
        }
    }
}

// Preview-only helpers and sample
data class ComparatorUiSample(
    val tariffName: String = "2.0TD",
    val powerRows: List<Pair<String, String>> = listOf(
        "P1" to "5.50 kW",
        "P2" to "5.50 kW",
    ),
    val energyRows: List<Pair<String, String>> = listOf(
        "P1" to "438.00 kWh",
        "P2" to "407.00 kWh",
        "P3" to "454.00 kWh",
    ),
    val extras: List<Pair<String, String>> = listOf(
        "IMPUESTO ELÉCTRICO" to "5.11%",
        "IVA" to "21%",
    )
)

@Preview(showBackground = true)
@Composable
private fun ComparatorViewPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val sample = ComparatorUiSample()
            ComparatorView(
                tariffName = sample.tariffName,
                annualConsumptionTitle = "CONSUMO ANUAL",
                powerTermRows = sample.powerRows,
                energyConsumedRows = sample.energyRows,
                extraServices = sample.extras,
                totalsTitle = "COSTE ANUAL FACTURA ELÉCTRICA"
            )
        }
    }
}
