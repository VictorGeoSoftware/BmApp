package com.briel.marnisos.brielapp.ui.views.pricetable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.briel.marnisos.brielapp.domain.models.FeeModel
import com.briel.marnisos.brielapp.ui.theme.TableBlue
import com.briel.marnisos.brielapp.ui.theme.TableBorder
import com.briel.marnisos.brielapp.ui.theme.TableText
import com.briel.marnisos.brielapp.ui.theme.TableTextSecondary
import java.util.Locale

@Composable
fun PriceTable(
    feeList: List<FeeModel>
) {
    Column {
        // Table Header
        PriceTableHeaderRow()

        feeList.forEach { fee ->
            PriceTableRow(
                row = fee
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
    row: FeeModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
                text = row.feeName,
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
                text = row.contractedPower ?: "",
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
            row.prices().forEach { price ->
                val price = if (price == null || price == 0.0) {
                    "—"
                } else {
                    String.format(
                        Locale.getDefault(),
                        "%.6f",
                        price
                    )
                }

                Text(
                    text = price,
                    fontSize = 11.sp,
                    color = TableText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
