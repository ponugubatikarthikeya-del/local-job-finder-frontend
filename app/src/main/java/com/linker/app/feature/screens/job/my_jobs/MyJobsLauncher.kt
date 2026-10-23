package com.linker.app.feature.screens.job.my_jobs

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linker.app.core.data.models.HomeJobDto

@Composable
fun MyJobsLauncher(
    viewModel: MyJobsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToJobDetails: (HomeJobDto) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MyJobsEffect.NavigateBack -> onNavigateBack()
                is MyJobsEffect.NavigateToJobDetails -> onNavigateToJobDetails(effect.job)
                is MyJobsEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    MyJobsUi(
        state = state,
        onEvent = viewModel::onEvent
    )
}
