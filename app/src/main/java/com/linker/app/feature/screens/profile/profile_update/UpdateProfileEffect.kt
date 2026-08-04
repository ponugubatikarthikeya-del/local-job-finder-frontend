package com.linker.app.feature.screens.profile.profile_update

sealed interface UpdateProfileEffect {
    object NavigateBack : UpdateProfileEffect
    data class ShowToast(val message: String) : UpdateProfileEffect
}
