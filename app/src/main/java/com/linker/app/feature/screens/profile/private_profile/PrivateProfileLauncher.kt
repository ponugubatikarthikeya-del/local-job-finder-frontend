package com.linker.app.feature.screens.profile.private_profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PrivateProfileLauncher(
    viewModel: PrivateProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onEditProfile: () -> Unit,
    onViewPublicProfile: () -> Unit,
    onLogoutComplete: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is PrivateProfileEffect.NavigateBack -> onNavigateBack()
                is PrivateProfileEffect.NavigateToEditProfile -> onEditProfile()
                is PrivateProfileEffect.NavigateToPublicProfile -> onViewPublicProfile()
                is PrivateProfileEffect.OnLogoutComplete -> onLogoutComplete()
            }
        }
    }

    PrivateProfileUi(
        state = state,
        onEvent = viewModel::onEvent
    )
}
