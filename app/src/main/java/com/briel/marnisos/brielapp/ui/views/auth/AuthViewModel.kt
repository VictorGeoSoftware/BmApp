package com.briel.marnisos.brielapp.ui.views.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.briel.marnisos.brielapp.domain.usecases.GetCurrentAuthUserUseCase
import com.briel.marnisos.brielapp.domain.usecases.GetFirebaseIdTokenUseCase
import com.briel.marnisos.brielapp.domain.usecases.LoginWithEmailUseCase
import com.briel.marnisos.brielapp.domain.usecases.SyncAuthenticatedUserDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val getCurrentAuthUserUseCase: GetCurrentAuthUserUseCase,
    private val getFirebaseIdTokenUseCase: GetFirebaseIdTokenUseCase,
    private val syncAuthenticatedUserDataUseCase: SyncAuthenticatedUserDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(isAuthenticated = getCurrentAuthUserUseCase() != null)
    )
    val uiState: StateFlow<AuthUiState> = _uiState

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { current ->
                current.copy(isLoading = true, errorMessage = null)
            }

            val loginResult = loginWithEmailUseCase(email.trim(), password)
            val user = loginResult.getOrElse { error ->
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        errorMessage = mapLoginError(error)
                    )
                }
                return@launch
            }

            val token = getFirebaseIdTokenUseCase(forceRefresh = true).getOrElse { error ->
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to obtain Firebase token"
                    )
                }
                return@launch
            }

            syncAuthenticatedUserDataUseCase(idToken = token, userData = user)
                .onSuccess {
                    _uiState.update { current ->
                        current.copy(isAuthenticated = true, isLoading = false, errorMessage = null)
                    }
                }
                .onFailure { error ->
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to sync user data"
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { current ->
            current.copy(errorMessage = null)
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
}
