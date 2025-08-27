package com.briel.marnisos.brielapp.ui.views.pricetable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.briel.marnisos.brielapp.domain.models.EnergyTerm
import com.briel.marnisos.brielapp.domain.models.FeeModel
import com.briel.marnisos.brielapp.ui.theme.TableBlue

@Composable
fun EnergyTermTableCard(
    table: EnergyTerm
) {
    SubTable(
        tableName = table.baseClassicPrice.title,
        feeList = table.baseClassicPrice.fees
    )

    Spacer(modifier = Modifier.height(16.dp))

    SubTable(
        tableName = table.uniqueClassicPrice.title,
        feeList = table.uniqueClassicPrice.fees
    )
}

@Composable
private fun SubTable(
    tableName: String,
    feeList: List<FeeModel>,
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
                    text = tableName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Table Content
            PriceTable(
                feeList = feeList
            )
        }
    }
}
