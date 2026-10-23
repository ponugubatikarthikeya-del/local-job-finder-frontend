package com.linker.app.feature.screens.home

import com.linker.app.core.data.models.HomeJobDto

sealed interface HomeEffect {
    data object OpenDrawer : HomeEffect
    data object NavigateToPrivateProfile : HomeEffect
    data class NavigateToPublicProfile(val jobId: Long) : HomeEffect
    data class NavigateToPublicCardDetails(val job: HomeJobDto) : HomeEffect
    data object NavigateToNotification : HomeEffect
    data object NavigateToChangeAddress : HomeEffect
}
