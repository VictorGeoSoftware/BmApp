package com.briel.marnisos.brielapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.briel.marnisos.brielapp.ui.components.PriceTableScreenView
import com.briel.marnisos.brielapp.ui.theme.BrielAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrielAppTheme {
                PriceTableScreenView()
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 480
)
@Composable
fun PriceTablePreview() {
    BrielAppTheme {
        PriceTableScreenView()
    }
}