package com.linker.app.feature.screens.profile.private_profile

import com.linker.app.core.data.models.User

data class PrivateProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
