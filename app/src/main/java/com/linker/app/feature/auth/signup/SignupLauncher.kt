package com.linker.app.feature.auth.signup

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignupLauncher(
    viewModel: SignupViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
//    onSignupSuccess: () -> Unit,
    onNavigateToOtpVerification: (String) -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SignupEffect.NavigateToLogin -> onNavigateToLogin()
//                is SignupEffect.SignupSuccess -> onSignupSuccess()
                is SignupEffect.NavigateToOtpVerification -> onNavigateToOtpVerification(effect.email)
                is SignupEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    SignupUi(
        state = state,
        onEvent = viewModel::onEvent
    )
}
