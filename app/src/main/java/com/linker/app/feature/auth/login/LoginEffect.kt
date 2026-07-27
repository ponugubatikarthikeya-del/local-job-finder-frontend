package com.linker.app.feature.auth.login

import com.linker.app.feature.auth.signup.SignupEffect

sealed interface LoginEffect {

    object NavigateToSignup : LoginEffect

    object NavigateToForgotPassword : LoginEffect

    data class NavigateToOtpVerification(val email: String) : LoginEffect

//    object NavigateToHome : LoginEffect

    data class ShowError(val message: String) : LoginEffect

    object NavigateToLoadingScreen : LoginEffect

//    object LoginSuccess : LoginEffect

}