package com.linker.app.feature.screens.profile.public_profile

sealed interface PublicProfileEffect {
    data object NavigateBack : PublicProfileEffect
}
