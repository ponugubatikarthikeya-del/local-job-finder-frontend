package com.linker.app.feature.auth.signup

sealed interface SignupEvent {
    data class OnUserNameChanged(val value: String) : SignupEvent
    data class OnPhoneChanged(val value: String) : SignupEvent
    data class OnEmailChanged(val value: String) : SignupEvent
    data class OnPasswordChanged(val value: String) : SignupEvent

    object OnTogglePasswordVisibility : SignupEvent

    object OnSignupBtnClicked : SignupEvent
    object OnGoogleAuthClicked : SignupEvent
    object OnFacebookAuthClicked : SignupEvent
    object OnNavigateLoginClicked : SignupEvent

    object OnNavigateToOtpVerificationClicked : SignupEvent
}
