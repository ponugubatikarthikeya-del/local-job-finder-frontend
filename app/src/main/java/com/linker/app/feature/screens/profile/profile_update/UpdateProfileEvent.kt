package com.linker.app.feature.screens.profile.profile_update

sealed interface UpdateProfileEvent {
    data class OnUsernameChanged(val value: String) : UpdateProfileEvent
    data class OnPhoneChanged(val value: String) : UpdateProfileEvent
    data class OnHeadlineChanged(val value: String) : UpdateProfileEvent
    data class OnDobChanged(val value: String) : UpdateProfileEvent
    data class OnSkillsChanged(val value: String) : UpdateProfileEvent
    data class OnCityChanged(val value: String) : UpdateProfileEvent
    data class OnBioChanged(val value: String) : UpdateProfileEvent
    object OnSaveClicked : UpdateProfileEvent
    object OnBackClicked : UpdateProfileEvent
}
