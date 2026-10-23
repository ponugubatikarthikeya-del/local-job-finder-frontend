package com.linker.app.feature.screens.profile.public_profile

import com.linker.app.core.data.models.UserDto

data class PublicProfileUistate(
    val user: UserDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
