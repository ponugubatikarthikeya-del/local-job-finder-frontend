package com.linker.app.feature.screens.home

import com.linker.app.core.data.models.HomeJobDto

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class HomeUiState(
    val jobsPagingDataFlow: Flow<PagingData<HomeJobDto>> = emptyFlow(),
    val address: String = "Detecting location...",
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val isLoading: Boolean = false
)
