package com.linker.app.feature.screens.profile.user_data_receive

data class UserDataTakenUiState(
    val cityName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
