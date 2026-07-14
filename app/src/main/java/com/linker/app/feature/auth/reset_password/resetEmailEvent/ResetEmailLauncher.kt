package com.linker.app.feature.auth.reset_password.resetEmailEvent

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ResetEmailLauncher(
    viewModel: ResetEmailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onSendSuccess: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ResetEmailEffect.SendSuccess -> onSendSuccess()
                is ResetEmailEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    ResetEmailUi(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = onNavigateBack
    )
}
