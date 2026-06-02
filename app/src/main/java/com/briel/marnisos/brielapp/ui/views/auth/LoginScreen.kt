package com.briel.marnisos.brielapp.ui.views.auth

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.briel.marnisos.brielapp.R
import com.briel.marnisos.brielapp.ui.theme.BrielAppTheme

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onLoginClicked: (email: String, password: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val colorScheme = MaterialTheme.colorScheme
    val isFormValid = email.isNotBlank() && password.isNotBlank()
    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = colorScheme.onSurface,
        unfocusedTextColor = colorScheme.onSurface,
        focusedContainerColor = colorScheme.surface,
        unfocusedContainerColor = colorScheme.surface,
        focusedPlaceholderColor = colorScheme.onSurfaceVariant,
        unfocusedPlaceholderColor = colorScheme.onSurfaceVariant,
        focusedBorderColor = colorScheme.primary,
        unfocusedBorderColor = colorScheme.outline,
        cursorColor = colorScheme.primary
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        Image(
            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
            contentDescription = stringResource(id = R.string.login_brand_icon_content_description),
            modifier = Modifier
                .height(96.dp)
                .fillMaxWidth(0.27f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(id = R.string.login_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            singleLine = true,
            placeholder = { Text(stringResource(id = R.string.login_email_placeholder)) },
            colors = inputColors
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            placeholder = { Text(stringResource(id = R.string.login_password_placeholder)) },
            colors = inputColors
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onLoginClicked(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading && isFormValid
        ) {
            Text(
                if (uiState.isLoading) {
                    stringResource(id = R.string.login_button_loading)
                } else {
                    stringResource(id = R.string.login_button)
                }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedButton(
            onClick = {},
            enabled = !uiState.isLoading
        ) {
            Text(text = stringResource(id = R.string.login_continue_with_google))
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.login_no_account),
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(id = R.string.login_request_access),
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.tertiary,
                modifier = Modifier.padding(start = 6.dp)
            )
        }

        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(name = "Login - Light", showBackground = true)
@Composable
private fun LoginScreenPreviewLight() {
    BrielAppTheme(darkTheme = false) {
        LoginScreen(
            uiState = AuthUiState(),
            onLoginClicked = { _, _ -> }
        )
    }
}

@Preview(
    name = "Login - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun LoginScreenPreviewDark() {
    BrielAppTheme(darkTheme = true) {
        LoginScreen(
            uiState = AuthUiState(),
            onLoginClicked = { _, _ -> }
        )
    }
}
