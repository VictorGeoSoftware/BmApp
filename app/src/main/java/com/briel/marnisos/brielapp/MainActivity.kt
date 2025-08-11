package com.briel.marnisos.brielapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.briel.marnisos.brielapp.ui.components.PriceTableScreen
import com.briel.marnisos.brielapp.ui.theme.BrielAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrielAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PriceTableScreen(
                        modifier = Modifier.padding(innerPadding),
                        onAddNewTable = {
                            // Handle add new table action
                        },
                        onExport = {
                            // Handle export action
                        }
                    )
                }
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
        PriceTableScreen()
    }
}