package com.linker.app.feature.screens.home

import com.linker.app.core.data.models.HomeJobCard

sealed interface HomeEvent {
    data object OnRefresh : HomeEvent
    data class OnSearchQueryChanged(val query: String) : HomeEvent
    data object OnSearchClicked : HomeEvent
    data class OnCategorySelected(val category: String?) : HomeEvent
    data class OnJobClicked(val job: HomeJobCard) : HomeEvent
    data object OnProfileClicked : HomeEvent
    data object OnNotificationClicked : HomeEvent
    data object OnMenuClicked : HomeEvent
}
