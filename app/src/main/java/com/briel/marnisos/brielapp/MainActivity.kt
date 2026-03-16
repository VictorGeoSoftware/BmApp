package com.briel.marnisos.brielapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.briel.marnisos.brielapp.ui.theme.BrielAppTheme
import com.briel.marnisos.brielapp.ui.views.ComparatorScreen
import com.briel.marnisos.brielapp.ui.views.auth.AuthViewModel
import com.briel.marnisos.brielapp.ui.views.auth.LoginScreen
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrielAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authViewModel: AuthViewModel = koinViewModel()
                    val authState by authViewModel.uiState.collectAsState()

                    if (authState.isAuthenticated) {
                        ComparatorScreen()
                    } else {
                        LoginScreen(
                            uiState = authState,
                            onLoginClicked = authViewModel::loginWithEmail
                        )
                    }
                }
            }
        }
    }
}
