package com.linker.app.feature.auth.OtpVerification

data class OtpUiState(
    val otp: String = "",
    val email: String = "",
    val resetKey : String = "",
    val timerTicks: Int = 300,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
