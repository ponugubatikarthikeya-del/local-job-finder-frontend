package com.linker.app.feature.screens.job.view_applicants

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ViewApplicantsLauncher(
    jobId: Long,
    viewModel: ViewApplicantsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(jobId) {
        viewModel.onEvent(ViewApplicantsEvent.LoadApplicants(jobId))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ViewApplicantsEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    ViewApplicantsUi(
        state = state,
        onEvent = { viewModel.onEvent(it) }
    )
}
