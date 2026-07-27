package com.linker.app.feature.screens.home

import com.linker.app.core.data.models.JobCard

data class HomeUiState(
    val jobs: List<JobCard> = emptyList(),
    val isLoading: Boolean = false
)
