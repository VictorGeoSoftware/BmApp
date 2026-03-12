package com.briel.marnisos.brielapp.ui.views.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onLoginClicked: (email: String, password: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val inputTextColor = Color(0xFF0F172A)
    val inputPlaceholderColor = Color(0xFF475569)
    val inputBorderColor = Color(0xFF64748B)
    val focusedBorderColor = Color(0xFF1D4ED8)
    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = inputTextColor,
        unfocusedTextColor = inputTextColor,
        focusedLabelColor = focusedBorderColor,
        unfocusedLabelColor = inputPlaceholderColor,
        focusedPlaceholderColor = inputPlaceholderColor,
        unfocusedPlaceholderColor = inputPlaceholderColor,
        focusedBorderColor = focusedBorderColor,
        unfocusedBorderColor = inputBorderColor,
        cursorColor = focusedBorderColor
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            enabled = !uiState.isLoading,
            singleLine = true,
            label = { Text("Email") },
            colors = inputColors
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            enabled = !uiState.isLoading,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Password") },
            colors = inputColors
        )

        Button(
            onClick = { onLoginClicked(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank()
        ) {
            Text(if (uiState.isLoading) "Logging in..." else "Login")
        }

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}
