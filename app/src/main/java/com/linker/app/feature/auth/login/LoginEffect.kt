package com.linker.app.feature.auth.login

import com.linker.app.feature.auth.signup.SignupEffect

sealed interface LoginEffect {

    object NavigateToSignup : LoginEffect

    object NavigateToForgotPassword : LoginEffect

    object NavigateToOtpVerification : LoginEffect

    object NavigateToHome : LoginEffect

    data class ShowError(val message: String) : LoginEffect

//    object LoginSuccess : LoginEffect

}