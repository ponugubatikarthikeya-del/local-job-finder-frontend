package com.linker.app.feature.auth.loading

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoadingLauncher(
    viewModel: LoadingViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToDetailsTake: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is LoadingEffect.NavigateToHome -> onNavigateToHome()
                is LoadingEffect.NavigateToDetailsTake -> onNavigateToDetailsTake()
                is LoadingEffect.ShowError -> {
                    // Handle error if needed
                }
            }
        }
    }

    LoadingUi(state = state)
}
