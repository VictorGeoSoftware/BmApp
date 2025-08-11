package com.briel.marnisos.brielapp.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.briel.marnisos.brielapp.ui.theme.*

data class TariffRow(
    val id: String,
    val tariff: String,
    val powerContract: String,
    val prices: List<Double>
)

data class PriceTable(
    val id: String,
    val name: String,
    val rows: List<TariffRow>
)

@Composable
@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 480,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
fun PriceTableScreenPreview() {
    BrielAppTheme {
        PriceTableScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceTableScreen(
    modifier: Modifier = Modifier,
    onAddNewTable: () -> Unit = {},
    onExport: () -> Unit = {}
) {
    // Sample data based on the mockup
    val sampleTable = PriceTable(
        id = "te2",
        name = "Tarifa TE2",
        rows = listOf(
            TariffRow(
                id = "2.0TD",
                tariff = "2.0TD",
                powerContract = "Menor o igual a 15 kW",
                prices = listOf(0.090221, 0.01835, 0.0, 0.0, 0.0, 0.0)
            ),
            TariffRow(
                id = "3.0TD",
                tariff = "3.0TD",
                powerContract = "Mayor a 15 kW",
                prices = listOf(0.056489, 0.030717, 0.014308, 0.012717, 0.009009, 0.023442)
            ),
            TariffRow(
                id = "6.1TD",
                tariff = "6.1TD",
                powerContract = "—",
                prices = listOf(0.081512, 0.043939, 0.0206, 0.0168, 0.007925, 0.022236)
            ),
            TariffRow(
                id = "6.2TD",
                tariff = "6.2TD",
                powerContract = "—",
                prices = listOf(0.056407, 0.03258, 0.012426, 0.00977, 0.00579, 0.021244)
            )
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Header
        PriceTableHeader(
            onAddNewTable = onAddNewTable,
            onExport = onExport
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Price Table
        PriceTableCard(
            table = sampleTable
        )
    }
}

@Composable
private fun PriceTableHeader(
    onAddNewTable: () -> Unit,
    onExport: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionIcon(
                icon = MenuIcon,
                contentDescription = "Menu",
                onClick = { /* Handle menu click */ },
                tint = TableText
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Your Price Tables",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TableText
            )
        }
        
        Row {
            Button(
                onClick = onAddNewTable,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TableBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Icon(
                    imageVector = PlusIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add New Table",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            OutlinedButton(
                onClick = onExport,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TableText
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, TableBorder),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Icon(
                    imageVector = ExportIcon,
                    contentDescription = null,
                    tint = TableText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Export",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun PriceTableCard(
    table: PriceTable
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Table Title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TableBlue)
                    .padding(16.dp)
            ) {
                Text(
                    text = table.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            // Table Content
            PriceTable(
                table = table
            )
        }
    }
}

@Composable
private fun PriceTable(
    table: PriceTable
) {
    Column {
        // Table Header
        PriceTableHeaderRow()
        
        // Table Rows
        table.rows.forEach { row ->
            PriceTableRow(
                row = row
            )
        }
    }
}

@Composable
private fun PriceTableHeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TableBlue)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Tariff column
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "TARIFA",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        
        // Power Contract column
        Box(
            modifier = Modifier.weight(1.5f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "POTENCIA CONTRATADA",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        
        // Price columns header
        Column(
            modifier = Modifier.weight(3f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "PRECIO POTENCIA (€/kWdia)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("P1", "P2", "P3", "P4", "P5", "P6").forEach { period ->
                    Text(
                        text = period,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        // Actions column
        Spacer(modifier = Modifier.width(60.dp))
    }
}

@Composable
private fun PriceTableRow(
    row: TariffRow
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (row.id == "2.0TD" || row.id == "6.2TD") TableGray else Color.White)
            .border(
                width = 0.5.dp,
                color = TableBorder
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Tariff
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = row.tariff,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TableText,
                textAlign = TextAlign.Center
            )
        }
        
        // Power Contract
        Box(
            modifier = Modifier.weight(1.5f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = row.powerContract,
                fontSize = 12.sp,
                color = TableTextSecondary,
                textAlign = TextAlign.Center
            )
        }
        
        // Prices
        Row(
            modifier = Modifier.weight(3f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            row.prices.forEach { price ->
                Text(
                    text = if (price == 0.0) "—" else String.format("%.6f", price),
                    fontSize = 11.sp,
                    color = TableText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
