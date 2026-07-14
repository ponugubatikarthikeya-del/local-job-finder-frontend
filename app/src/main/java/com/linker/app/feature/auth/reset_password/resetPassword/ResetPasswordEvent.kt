package com.linker.app.feature.auth.reset_password.resetPassword

sealed interface ResetPasswordEvent {
    data class OnPasswordChanged(val value: String) : ResetPasswordEvent
    data class OnConfirmPasswordChanged(val value: String) : ResetPasswordEvent
    object OnTogglePasswordVisibility : ResetPasswordEvent
    object OnResetClicked : ResetPasswordEvent
}
