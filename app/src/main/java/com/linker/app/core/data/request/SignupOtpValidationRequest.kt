package com.linker.app.core.data.request

import kotlinx.serialization.Serializable

@Serializable
data class SignupOtpValidationRequest(
    val email: String = "",
    val otp: String = ""
)
