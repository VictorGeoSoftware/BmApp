package com.briel.marnisos.brielapp.ui.components.tables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
fun DynamicTableColumnView(
    modifier: Modifier = Modifier,
    values: List<String>,
) {
    val colors = MaterialTheme.extendedColors

    Column(
        modifier = modifier.then(
            Modifier.border(
                width = 1.dp,
                color = colors.tableBorder
            )
        )
    ) {
        values.forEachIndexed { index, value ->
            Column (
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(12.dp),
                    text = value,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.tableText,
                    textAlign = TextAlign.Center
                )

                val isLast = index == values.lastIndex
                if (!isLast) {
                    HorizontalSeparator()
                }
            }
        }
    }
}

@Composable
@Preview
fun DynamicTableColumnPreview() {
    DynamicTableColumnView(
        values = listOf("Value 1", "Value 2", "Value 3")
    )
}