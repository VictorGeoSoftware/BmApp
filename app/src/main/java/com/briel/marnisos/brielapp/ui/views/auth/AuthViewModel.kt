package com.briel.marnisos.brielapp.ui.views.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.briel.marnisos.brielapp.domain.monitoring.CrashErrorCategory
import com.briel.marnisos.brielapp.domain.monitoring.CrashReporter
import com.briel.marnisos.brielapp.domain.error.AccessDeniedException
import com.briel.marnisos.brielapp.domain.usecases.ClearCurrentUserConditionsUseCase
import com.briel.marnisos.brielapp.domain.usecases.ClearLastCompletedJobIdUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetCurrentAuthUserUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetDeviceIdUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetFirebaseIdTokenUseCase
import com.briel.marnisos.brielapp.domain.usecases.LoginWithEmailUseCase
import com.briel.marnisos.brielapp.domain.usecases.LoginWithGoogleUseCase
import com.briel.marnisos.brielapp.domain.usecases.LogoutUseCase
import com.briel.marnisos.brielapp.domain.usecases.SetUserOfflineUseCase
import com.briel.marnisos.brielapp.domain.usecases.SyncAuthenticatedUserDataUseCase
import com.briel.marnisos.brielapp.notifications.ForceLogoutEventBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val getCurrentAuthUserUseCase: GetCurrentAuthUserUseCase,
    private val getFirebaseIdTokenUseCase: GetFirebaseIdTokenUseCase,
    private val getDeviceIdUseCase: GetDeviceIdUseCase,
    private val syncAuthenticatedUserDataUseCase: SyncAuthenticatedUserDataUseCase,
    private val setUserOfflineUseCase: SetUserOfflineUseCase,
    private val clearCurrentUserConditionsUseCase: ClearCurrentUserConditionsUseCase,
    private val clearLastCompletedJobIdUseCase: ClearLastCompletedJobIdUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val crashReporter: CrashReporter,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(isAuthenticated = getCurrentAuthUserUseCase() != null)
    )
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        // The FCM force-logout handler has already signed the user out
        // locally; here we just move the UI back to the login screen.
        viewModelScope.launch {
            ForceLogoutEventBus.events.collect {
                _uiState.update { current ->
                    current.copy(
                        isAuthenticated = false,
                        isLoading = false,
                        errorMessage = ACCESS_REVOKED_MESSAGE
                    )
                }
            }
        }
    }

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            crashReporter.setScreenContext("auth_login")
            crashReporter.setUseCaseContext("login_with_email")

            _uiState.update { current ->
                current.copy(isLoading = true, errorMessage = null)
            }

            val loginResult = loginWithEmailUseCase(email.trim(), password)
            val user = loginResult.getOrElse { error ->
                crashReporter.recordNonFatal(
                    throwable = error,
                    category = CrashErrorCategory.AUTHENTICATION,
                    operation = "login_with_email",
                )
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        errorMessage = mapLoginError(error)
                    )
                }
                return@launch
            }

            val token = getFirebaseIdTokenUseCase(forceRefresh = true).getOrElse { error ->
                crashReporter.recordNonFatal(
                    throwable = error,
                    category = CrashErrorCategory.AUTHENTICATION,
                    operation = "get_firebase_id_token",
                )
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to obtain Firebase token"
                    )
                }
                return@launch
            }

            val userWithDevice = user.copy(phoneUuid = getDeviceIdUseCase())
            syncAuthenticatedUserDataUseCase(idToken = token, userData = userWithDevice)
                .onSuccess {
                    _uiState.update { current ->
                        current.copy(isAuthenticated = true, isLoading = false, errorMessage = null)
                    }
                }
                .onFailure { error -> handleSyncFailure(error) }
        }
    }

    fun loginWithGoogle(googleIdToken: String) {
        viewModelScope.launch {
            crashReporter.setScreenContext("auth_login")
            crashReporter.setUseCaseContext("login_with_google")

            _uiState.update { current ->
                current.copy(isLoading = true, errorMessage = null)
            }

            val user = loginWithGoogleUseCase(googleIdToken).getOrElse { error ->
                crashReporter.recordNonFatal(
                    throwable = error,
                    category = CrashErrorCategory.AUTHENTICATION,
                    operation = "login_with_google",
                )
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Google sign-in failed"
                    )
                }
                return@launch
            }

            val token = getFirebaseIdTokenUseCase(forceRefresh = true).getOrElse { error ->
                crashReporter.recordNonFatal(
                    throwable = error,
                    category = CrashErrorCategory.AUTHENTICATION,
                    operation = "get_firebase_id_token",
                )
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to obtain Firebase token"
                    )
                }
                return@launch
            }

            val userWithDevice = user.copy(phoneUuid = getDeviceIdUseCase())
            syncAuthenticatedUserDataUseCase(idToken = token, userData = userWithDevice)
                .onSuccess {
                    _uiState.update { current ->
                        current.copy(isAuthenticated = true, isLoading = false, errorMessage = null)
                    }
                }
                .onFailure { error -> handleSyncFailure(error) }
        }
    }

    fun clearError() {
        _uiState.update { current ->
            current.copy(errorMessage = null)
        }
    }

    fun logout() {
        viewModelScope.launch {
            crashReporter.setScreenContext("auth_logout")
            crashReporter.setUseCaseContext("logout")

            _uiState.update { current ->
                current.copy(isLoading = true, errorMessage = null)
            }

            val authUser = getCurrentAuthUserUseCase()
            val email = authUser?.email?.trim().orEmpty()
            val name = authUser?.displayName
                ?.takeIf { it.isNotBlank() }
                ?: email.substringBefore('@')

            if (email.isNotBlank()) {
                setUserOfflineUseCase(name = name, email = email).onFailure { error ->
                    crashReporter.recordNonFatal(
                        throwable = error,
                        category = CrashErrorCategory.BACKEND,
                        operation = "set_user_offline",
                    )
                }
            }

            runCatching {
                clearCurrentUserConditionsUseCase()
                clearLastCompletedJobIdUseCase()
                logoutUseCase()
            }.onSuccess {
                _uiState.update { current ->
                    current.copy(isAuthenticated = false, isLoading = false, errorMessage = null)
                }
            }.onFailure { error ->
                crashReporter.recordNonFatal(
                    throwable = error,
                    category = CrashErrorCategory.AUTHENTICATION,
                    operation = "logout",
                )
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to logout"
                    )
                }
            }
        }
    }

    private suspend fun handleSyncFailure(error: Throwable) {
        if (error is AccessDeniedException) {
            // Expected authorization rejection (allowlist / device binding).
            // The Firebase credential is valid, so we must sign the user out;
            // otherwise they would be auto-authenticated on the next launch and
            // bypass this check. Then surface a contact-administration message.
            runCatching { logoutUseCase() }
            _uiState.update { current ->
                current.copy(
                    isAuthenticated = false,
                    isLoading = false,
                    errorMessage = error.message?.takeIf { it.isNotBlank() } ?: ACCESS_DENIED_MESSAGE
                )
            }
            return
        }

        crashReporter.recordNonFatal(
            throwable = error,
            category = CrashErrorCategory.BACKEND,
            operation = "sync_authenticated_user_data",
        )
        _uiState.update { current ->
            current.copy(
                isLoading = false,
                errorMessage = error.message ?: "Failed to sync user data"
            )
        }
    }

    private fun mapLoginError(error: Throwable): String {
        val message = error.message.orEmpty().lowercase()
        return when {
            "invalid-credential" in message || "invalid credential" in message -> "Invalid email or password"
            "user-disabled" in message || "user disabled" in message -> "This account has been disabled"
            "too-many-requests" in message || "too many requests" in message -> "Too many attempts. Please try later"
            else -> error.message ?: "Unexpected error while logging in"
        }
    }

    private companion object {
        private const val ACCESS_DENIED_MESSAGE =
            "Esta cuenta no está autorizada para acceder. Contacta con el equipo de administración."
        private const val ACCESS_REVOKED_MESSAGE =
            "Tu acceso ha sido revocado. Contacta con el equipo de administración."
    }
}
