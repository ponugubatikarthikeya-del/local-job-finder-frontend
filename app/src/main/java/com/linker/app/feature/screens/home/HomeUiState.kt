package com.linker.app.feature.screens.home

import com.linker.app.core.data.models.HomeJobCard

data class HomeUiState(
    val jobs: List<HomeJobCard> = emptyList(),
    val address: String = "Detecting location...",
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val isLoading: Boolean = false
)
