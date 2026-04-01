package com.briel.marnisos.brielapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.briel.marnisos.brielapp.domain.usecases.GetCurrentAuthUserUseCase
import com.briel.marnisos.brielapp.domain.usecases.SetUserOfflineUseCase
import com.briel.marnisos.brielapp.domain.usecases.SetUserOnlineUseCase
import com.briel.marnisos.brielapp.ui.theme.BrielAppTheme
import com.briel.marnisos.brielapp.ui.views.MainView
import com.briel.marnisos.brielapp.ui.views.auth.AuthViewModel
import com.briel.marnisos.brielapp.ui.views.auth.LoginScreen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val getCurrentAuthUserUseCase: GetCurrentAuthUserUseCase by inject()
    private val setUserOnlineUseCase: SetUserOnlineUseCase by inject()
    private val setUserOfflineUseCase: SetUserOfflineUseCase by inject()

    private val postNotificationsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted && !NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            openAppNotificationSettings()
        }
    }

    override fun onResume() {
        super.onResume()
        sendUserActivity(isOnline = true)
    }

    override fun onStop() {
        sendUserActivity(isOnline = false)
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationsIfNeeded()
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
                        MainView()
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

    private fun requestNotificationsIfNeeded() {
        if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                postNotificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }

        openAppNotificationSettings()
    }

    private fun openAppNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        startActivity(intent)
    }

    private fun sendUserActivity(isOnline: Boolean) {
        val user = getCurrentAuthUserUseCase() ?: return
        val email = user.email?.trim().orEmpty()
        if (email.isBlank()) return

        val name = user.displayName
            ?.takeIf { it.isNotBlank() }
            ?: email.substringBefore('@')

        lifecycleScope.launch {
            if (isOnline) {
                setUserOnlineUseCase(name = name, email = email)
            } else {
                setUserOfflineUseCase(name = name, email = email)
            }
        }
    }
}
