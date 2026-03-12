package com.briel.marnisos.brielapp.ui.views.auth

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
