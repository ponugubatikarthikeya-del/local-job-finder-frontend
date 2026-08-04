package com.linker.app.feature.screens.home

import com.linker.app.core.data.models.HomeJobCard

sealed interface HomeEffect {
    data object OpenDrawer : HomeEffect
    data object NavigateToPublicProfile : HomeEffect
    data class NavigateToPublicCardDetails(val job: HomeJobCard) : HomeEffect
    data object NavigateToNotification : HomeEffect
}
