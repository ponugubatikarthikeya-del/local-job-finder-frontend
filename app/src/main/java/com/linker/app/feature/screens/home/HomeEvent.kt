package com.linker.app.feature.screens.home

import com.linker.app.core.data.models.HomeJobDto

sealed interface HomeEvent {
    data object OnRefresh : HomeEvent
    data class OnSearchQueryChanged(val query: String) : HomeEvent
    data object OnSearchClicked : HomeEvent
    data class OnCategorySelected(val category: String?) : HomeEvent
    data class OnJobClicked(val job: HomeJobDto) : HomeEvent
    data class OnProfileClicked(val job: HomeJobDto) : HomeEvent
    data object OnNotificationClicked : HomeEvent
    data object OnMenuClicked : HomeEvent
    data object OnChangeLocationClicked : HomeEvent
}
