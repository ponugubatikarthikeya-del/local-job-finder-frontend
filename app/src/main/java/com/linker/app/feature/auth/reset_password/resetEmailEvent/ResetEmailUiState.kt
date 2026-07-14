package com.linker.app.feature.auth.reset_password.resetEmailEvent

data class ResetEmailUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
