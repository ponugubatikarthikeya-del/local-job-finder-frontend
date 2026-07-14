package com.linker.app.feature.auth.signup

sealed interface SignupEffect {
    object NavigateToLogin : SignupEffect

    object NavigateToOtpVerification : SignupEffect
    data class ShowError(val message: String) : SignupEffect
    object SignupSuccess : SignupEffect
}
