package com.linker.app.feature.auth.reset_password.resetPassword

sealed interface ResetPasswordEffect {
    object ResetSuccess : ResetPasswordEffect
    data class ShowError(val message: String) : ResetPasswordEffect
}
