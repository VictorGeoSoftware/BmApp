package com.briel.marnisos.brielapp.ui.components.tables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.briel.marnisos.brielapp.ui.theme.extendedColors

@Composable
fun SideTitleTableView(
    modifier: Modifier = Modifier,
    sideTitle: String,
    leftValues: List<String>,
    rightValues: List<String>,
) {
    val colors = MaterialTheme.extendedColors

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.weight(1.0f)
                .border(
                    width = 1.dp,
                    color = colors.tableBorder
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f).padding(12.dp),
                text = sideTitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.tableText,
                textAlign = TextAlign.Center,
            )

            DynamicTableColumnView(
                modifier = Modifier.weight(1f),
                values = leftValues,
            )
        }


        DynamicTableColumnView(
            modifier = Modifier.weight(1f),
            values = rightValues,
        )
    }
}

@Composable
@Preview
fun SideTitleTablePreview() {
    SideTitleTableView(
        sideTitle = "Termino de Potencia",
        leftValues = listOf("P1", "P2", "P3", "P4", "P5", "P6"),
        rightValues = listOf("P1", "P2", "P3", "P4", "P5", "P6"),
    )
}