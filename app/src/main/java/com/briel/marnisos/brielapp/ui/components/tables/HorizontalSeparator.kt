package com.briel.marnisos.brielapp.ui.components.tables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.briel.marnisos.brielapp.ui.theme.TableBorder


@Composable
fun HorizontalSeparator(
    modifier: Modifier = Modifier,
    color: Color = TableBorder,
    thickness: Dp = 1.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .background(color)
    )
}
