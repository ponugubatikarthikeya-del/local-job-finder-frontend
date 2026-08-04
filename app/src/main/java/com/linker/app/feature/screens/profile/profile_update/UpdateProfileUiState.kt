package com.linker.app.feature.screens.profile.profile_update

data class UpdateProfileUiState(
    val username: String = "",
    val phone: String = "",
    val headline: String = "",
    val dateOfBirth: String = "",
    val skills: String = "",
    val cityName: String = "",
    val userDescription: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)
