package com.linker.app.feature.screens.profile.public_profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PublicProfileLauncher(
    jobId: Long,
    viewModel: PublicProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(jobId) {
        viewModel.loadProfile(jobId)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is PublicProfileEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    PublicProfileUi(
        state = state,
        onEvent = viewModel::onEvent
    )
}
