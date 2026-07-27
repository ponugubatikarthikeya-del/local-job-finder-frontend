package com.linker.app.feature.auth.OtpVerification

sealed interface OtpEffect {
    data class VerificationSuccess(val otp: String? = null) : OtpEffect
    data class ShowError(val message: String) : OtpEffect
}
