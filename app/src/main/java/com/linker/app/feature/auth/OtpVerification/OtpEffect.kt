package com.linker.app.feature.auth.OtpVerification

sealed interface OtpEffect {
    object VerificationSuccess : OtpEffect
    data class ShowError(val message: String) : OtpEffect
}
