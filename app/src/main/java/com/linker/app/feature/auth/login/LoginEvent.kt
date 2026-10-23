package com.linker.app.feature.auth.login

import com.linker.app.feature.auth.signup.SignupEvent

sealed interface LoginEvent {

    data class OnEmailChanged(val value : String) : LoginEvent

    data class OnPasswordChanged(val value : String) : LoginEvent

    object OnTogglePasswordVisibility : LoginEvent

    object OnLoginBtnClicked : LoginEvent

    object OnGoogleAuthClicked : LoginEvent
    object OnFacebookAuthClicked : LoginEvent

    object OnNavigateSignupClicked : LoginEvent

    object OnNavigateForgotPasswordClicked : LoginEvent

    object OnNavigateOtpVerificationClicked : LoginEvent

}
