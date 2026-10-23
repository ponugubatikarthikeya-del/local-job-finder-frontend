package com.linker.app.feature.auth.login

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginLauncher(
    viewModel: LoginViewModel = hiltViewModel(),
    NavigateToSignup : () -> Unit,
    NavigateToForgotPassword : () -> Unit,
    NavigateToOtpVerification : (String) -> Unit,
    NavigateToLoading : () -> Unit
) {

    val context = LocalContext.current

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
               is LoginEffect.NavigateToLoadingScreen -> NavigateToLoading()
                is LoginEffect.NavigateToSignup -> NavigateToSignup()
                is LoginEffect.NavigateToForgotPassword -> NavigateToForgotPassword()
                is LoginEffect.NavigateToOtpVerification -> NavigateToOtpVerification(effect.email)
                is LoginEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    LoginUi(
        state = state,
        onEvent = viewModel :: onEvent
    )

}
