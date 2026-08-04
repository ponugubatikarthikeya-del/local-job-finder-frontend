package com.linker.app.feature.screens.job.public_job_details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PublicJobDetailsLauncher(
    jobId: Long,
    showApplyButton: Boolean,
    viewModel: PublicJobDetailsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(jobId, showApplyButton) {
        viewModel.loadJob(jobId, showApplyButton)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is PublicJobDetailsEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    PublicJobDetailsUi(
        state = state,
        onEvent = viewModel::onEvent
    )
}
