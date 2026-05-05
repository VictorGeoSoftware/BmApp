package com.briel.marnisos.brielapp.ui.components.tables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.briel.marnisos.brielapp.ui.theme.extendedColors

@Composable
fun ExtraServicesAndTaxesRow(
    modifier: Modifier = Modifier,
    iva: String,
    electricityTax: String,
    fixedAmountInputValue: String = "",
    onFixedAmountInputChange: ((String) -> Unit),
) {
    val colors = extendedColors

    Column(
        modifier = modifier.then(
            Modifier.border(
                width = 1.dp,
                color = colors.tableBorder
            )
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            value = fixedAmountInputValue,
            onValueChange = { input ->
                if (input.matches(Regex("^\\d*([.,]\\d{0,2})?$"))) {
                    onFixedAmountInputChange(input)
                }
            },
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = colors.tableText
            ),
            placeholder = {
                Text(
                    text = "0.00",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = colors.tableText,
                unfocusedTextColor = colors.tableText,
                focusedBorderColor = colors.tableBorder,
                unfocusedBorderColor = colors.tableBorder,
                cursorColor = colors.tableText
            )
        )

        HorizontalSeparator()

        Text(
            modifier = Modifier.padding(12.dp),
            text = electricityTax,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colors.tableText,
            textAlign = TextAlign.Center
        )

        HorizontalSeparator()

        Text(
            modifier = Modifier.padding(12.dp),
            text = iva,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colors.tableText,
            textAlign = TextAlign.Center
        )
    }
}
