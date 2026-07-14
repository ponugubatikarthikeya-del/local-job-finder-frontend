package com.linker.app.feature.auth.OtpVerification

sealed interface OtpEvent {
    data class OnOtpChanged(val value: String) : OtpEvent
    object OnVerifyClicked : OtpEvent
    object OnResendClicked : OtpEvent
}
