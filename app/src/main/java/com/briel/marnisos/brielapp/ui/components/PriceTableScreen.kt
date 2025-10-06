package com.briel.marnisos.brielapp.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.briel.marnisos.brielapp.domain.models.PriceTablesModel
import com.briel.marnisos.brielapp.ui.theme.BrielAppTheme
import com.briel.marnisos.brielapp.ui.theme.TableText
import com.briel.marnisos.brielapp.ui.views.pricetable.EnergyTermTableCard
import com.briel.marnisos.brielapp.ui.views.pricetable.PowerTermTableCard
import com.briel.marnisos.brielapp.ui.views.pricetable.ComparatorViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 480,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
fun PriceTableScreenPreview() {
    BrielAppTheme {
        PriceTableScreen(priceTablesModel = PriceTablesModel.empty)
    }
}

@Composable
fun PriceTableScreenView(
    viewModel: ComparatorViewModel = koinViewModel()
) {
    val tables by viewModel.priceTablesModel.collectAsState()
    PriceTableScreen(priceTablesModel = tables)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceTableScreen(
    modifier: Modifier = Modifier,
    priceTablesModel: PriceTablesModel
) {
    val powerTermTable = priceTablesModel.powerTerm

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = priceTablesModel.companyName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TableText
        )

        Spacer(modifier = Modifier.height(24.dp))

        PowerTermTableCard(
            table = powerTermTable
        )

        Spacer(modifier = Modifier.height(24.dp))

        EnergyTermTableCard(
            table = priceTablesModel.energyTerm
        )
    }
}
