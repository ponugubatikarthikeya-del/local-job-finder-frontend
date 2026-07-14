package com.linker.app.feature.auth.reset_password.resetEmailEvent

sealed interface ResetEmailEvent {
    data class OnEmailChanged(val value: String) : ResetEmailEvent
    object OnSendClicked : ResetEmailEvent
}
