package com.linker.app.feature.auth.reset_password.resetEmailEvent

sealed interface ResetEmailEffect {
    data class SendSuccess(val email: String) : ResetEmailEffect
    data class ShowError(val message: String) : ResetEmailEffect
}
