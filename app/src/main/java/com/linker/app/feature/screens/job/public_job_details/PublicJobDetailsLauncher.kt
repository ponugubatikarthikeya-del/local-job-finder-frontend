package com.linker.app.feature.screens.job.public_job_details

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PublicJobDetailsLauncher(
    jobId: Long,
    showApplyButton: Boolean,
    showDeleteButton: Boolean = false,
    viewModel: PublicJobDetailsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToViewApplicants: (Long) -> Unit,
    onNavigateToPublicProfile: (Long) -> Unit,
    onNavigateToPrivateProfile: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(jobId, showApplyButton, showDeleteButton) {
        viewModel.loadJob(jobId, showApplyButton, showDeleteButton)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is PublicJobDetailsEffect.NavigateBack -> onNavigateBack()
                is PublicJobDetailsEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is PublicJobDetailsEffect.NavigateToViewApplicants -> {
                    onNavigateToViewApplicants(effect.jobId)
                }
                is PublicJobDetailsEffect.NavigateToProfile -> {
                    if (effect.isPrivate) {
                        onNavigateToPrivateProfile()
                    } else {
                        onNavigateToPublicProfile(effect.jobId)
                    }
                }
            }
        }
    }

    PublicJobDetailsUi(
        state = state,
        onEvent = viewModel::onEvent
    )
}
