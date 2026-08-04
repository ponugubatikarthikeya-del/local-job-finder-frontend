package com.linker.app.feature.screens.job.post_jobs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PostJobLauncher(
    viewModel: PostJobViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is PostJobEffect.NavigateBack -> onNavigateBack()
                is PostJobEffect.ShowSuccessToast -> onShowToast(effect.message)
                is PostJobEffect.ShowError -> onShowToast(effect.message)
            }
        }
    }

    PostJobUi(
        state = state,
        onEvent = viewModel::onEvent
    )
}
