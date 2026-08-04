package com.linker.app.feature.screens.job.my_jobs

import com.linker.app.core.data.models.HomeJobCard

data class MyJobsUiState(
    val jobs: List<HomeJobCard> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
