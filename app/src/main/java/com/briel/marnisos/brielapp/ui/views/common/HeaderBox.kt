package com.briel.marnisos.brielapp.ui.views.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.briel.marnisos.brielapp.ui.theme.extendedColors

@Composable
fun HeaderBox(
    text: String,
    background: Color,
    modifier: Modifier = Modifier,
    corner: Dp = 8.dp
) {
    val colors = MaterialTheme.extendedColors

    Box(
        modifier
            .background(background, RoundedCornerShape(corner))
            .border(1.dp, colors.sectionBorder, RoundedCornerShape(corner))
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
        )
    }
}
