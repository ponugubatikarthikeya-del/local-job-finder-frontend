package com.linker.app.feature.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linker.app.core.data.models.HomeJobCard

@Composable
fun HomeLauncher(
    viewModel: HomeViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit,
    onNavigateToPublicProfile: (Long) -> Unit,
    onNavigateToPublicCardDetails: (HomeJobCard) -> Unit,
    onNavigateToNotification: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.OpenDrawer -> onOpenDrawer()
                is HomeEffect.NavigateToPublicProfile -> onNavigateToPublicProfile(effect.jobId)
                is HomeEffect.NavigateToPublicCardDetails -> onNavigateToPublicCardDetails(effect.job)
                is HomeEffect.NavigateToNotification -> onNavigateToNotification()
            }
        }
    }

    HomeUi(
        state = state,
        onEvent = viewModel::onEvent
    )
}
