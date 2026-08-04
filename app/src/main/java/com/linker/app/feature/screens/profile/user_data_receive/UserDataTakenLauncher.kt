package com.linker.app.feature.screens.profile.user_data_receive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun UserDataTakenLauncher(
    viewModel: UserDataTakenViewModel = hiltViewModel(),
    onNavigateToLoading: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onNavigateToLoading()
        }
    }

    UserDataTakenUi(
        state = state,
        onEvent = { viewModel.onEvent(it) }
    )
}
