package com.linker.app.feature.auth.signup

sealed interface SignupEffect {
    object NavigateToLogin : SignupEffect
    data class NavigateToOtpVerification(val email: String) : SignupEffect

    data class ShowError(val message: String) : SignupEffect
//    object SignupSuccess : SignupEffect
}
