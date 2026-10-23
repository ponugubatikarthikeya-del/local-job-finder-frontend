package com.linker.app.core.data.request

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordOtpValidateRequest(
    val email: String = "",
    val otp: Int? = null
)
