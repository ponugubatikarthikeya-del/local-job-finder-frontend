package com.linker.app.feature.auth.signup

data class SignupUiState (
    val username: String = "",
    val phone: String = "",
    val email : String = "",
    val password : String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
