package com.linker.app.feature.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linker.app.core.data.models.HomeJobDto

@Composable
fun HomeLauncher(
    viewModel: HomeViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit,
    onNavigateToPrivateProfile: () -> Unit,
    onNavigateToPublicProfile: (Long) -> Unit,
    onNavigateToPublicCardDetails: (HomeJobDto) -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToChangeAddress: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.OpenDrawer -> onOpenDrawer()
                is HomeEffect.NavigateToPrivateProfile -> onNavigateToPrivateProfile()
                is HomeEffect.NavigateToPublicProfile -> onNavigateToPublicProfile(effect.jobId)
                is HomeEffect.NavigateToPublicCardDetails -> onNavigateToPublicCardDetails(effect.job)
                is HomeEffect.NavigateToNotification -> onNavigateToNotification()
                is HomeEffect.NavigateToChangeAddress -> onNavigateToChangeAddress()
            }
        }
    }

    HomeUi(
        state = state,
        onEvent = viewModel::onEvent
    )
}
