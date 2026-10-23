package com.linker.app.feature.screens.profile.private_profile

import com.linker.app.core.data.models.UserDto

data class PrivateProfileUiState(
    val user: UserDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
