package com.linker.app.feature.screens.profile.public_profile

sealed interface PublicProfileEvent {
    data object OnBackClicked : PublicProfileEvent
}
