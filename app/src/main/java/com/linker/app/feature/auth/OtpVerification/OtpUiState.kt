package com.linker.app.feature.auth.OtpVerification

data class OtpUiState(
    val otp: String = "",
    val email: String = "ponugubatikarthikeya@gmail.com",
    val timerTicks: Int = 30,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
