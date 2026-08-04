package com.linker.app.feature.auth.OtpVerification

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OtpLauncher(
    isResetFlow: Boolean, // Explicitly pass the flow mode down
    viewModel: OtpViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onVerificationSuccess: (String?) -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Sync the configuration flag explicitly when entering the composable screen
    LaunchedEffect(isResetFlow) {
        viewModel.setFlowMode(isResetFlow)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is OtpEffect.VerificationSuccess -> onVerificationSuccess(effect.otp)
                is OtpEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    OtpVerificationUi(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = onNavigateBack
    )
}
