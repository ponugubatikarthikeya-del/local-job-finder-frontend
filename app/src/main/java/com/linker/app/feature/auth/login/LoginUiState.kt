package com.linker.app.feature.auth.login

data class LoginUiState (

    val email : String = "",
    val password : String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null

)
