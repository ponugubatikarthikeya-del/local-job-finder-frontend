package com.linker.app.feature.screens.profile.private_profile

sealed interface PrivateProfileEffect {
    data object NavigateBack : PrivateProfileEffect
    data object NavigateToEditProfile : PrivateProfileEffect
    data object NavigateToPublicProfile : PrivateProfileEffect
    data object OnLogoutComplete : PrivateProfileEffect
}
