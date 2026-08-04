package com.linker.app.feature.screens.profile.public_profile

import com.linker.app.core.data.models.User

data class PublicProfileUistate(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
