package com.linker.app.feature.screens.job.my_jobs

import com.linker.app.core.data.models.HomeJobDto

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class MyJobsUiState(
    val jobsPagingDataFlow: Flow<PagingData<HomeJobDto>> = emptyFlow(),
    val isLoading: Boolean = false,
    val error: String? = null
)
