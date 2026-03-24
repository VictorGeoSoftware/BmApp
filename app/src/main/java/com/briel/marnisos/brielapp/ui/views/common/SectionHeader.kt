package com.briel.marnisos.brielapp.ui.views.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.briel.marnisos.brielapp.ui.theme.extendedColors

@Composable
fun SectionHeader(
    text: String,
    background: Color,
    modifier: Modifier = Modifier,
    corner: Dp = 8.dp
) {
    val colors = extendedColors

    Row(
        modifier = modifier
            .background(background, RoundedCornerShape(corner))
            .border(1.dp, colors.sectionBorder, RoundedCornerShape(corner))
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}