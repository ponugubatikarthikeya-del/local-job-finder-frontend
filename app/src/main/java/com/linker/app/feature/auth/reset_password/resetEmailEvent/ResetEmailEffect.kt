package com.linker.app.feature.auth.reset_password.resetEmailEvent

sealed interface ResetEmailEffect {
    object SendSuccess : ResetEmailEffect
    data class ShowError(val message: String) : ResetEmailEffect
}
