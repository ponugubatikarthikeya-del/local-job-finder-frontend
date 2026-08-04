package com.linker.app.feature.screens.profile.private_profile

sealed interface PrivateProfileEvent {
    data object OnRefresh : PrivateProfileEvent
    data object OnBackClicked : PrivateProfileEvent
    data object OnEditProfileClicked : PrivateProfileEvent
    data object OnViewPublicProfileClicked : PrivateProfileEvent
    data object OnLogoutClicked : PrivateProfileEvent
}
