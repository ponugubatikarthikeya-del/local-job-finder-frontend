package com.linker.app.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.app.feature.auth.signup.SignupEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.contracts.Effect

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel (){

    private val _state = MutableStateFlow(LoginUiState())
    val state : StateFlow<LoginUiState> = _state.asStateFlow()

    private val _effect = Channel<LoginEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent ( event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChanged -> {
                _state.update { it.copy(email = event.value) }
            }

            is LoginEvent.OnPasswordChanged -> {
                _state.update { it.copy(password = event.value) }
            }

            is LoginEvent.OnTogglePasswordVisibility -> {
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            is LoginEvent.OnNavigateSignupClicked->{
                sendEffect(LoginEffect.NavigateToSignup)
            }

            is LoginEvent.OnNavigateForgotPasswordClicked -> {
                sendEffect(LoginEffect.NavigateToForgotPassword)
            }

            is LoginEvent.OnNavigateOtpVerificationClicked -> {
                sendEffect(LoginEffect.NavigateToOtpVerification)
            }

            is LoginEvent.onNavigateHomeClicked -> {
                sendEffect(LoginEffect.NavigateToHome)
            }

            is LoginEvent.OnLoginBtnClicked -> {
                perform()
            }

            is LoginEvent.OnFacebookAuthClicked -> {}
            is LoginEvent.OnGoogleAuthClicked -> {}

        }

    }

    private fun sendEffect(effect : LoginEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    private fun perform() {

        if (_state.value.email.isBlank() || _state.value.password.isBlank()) {
            sendEffect(LoginEffect.ShowError("please fill the blanks"))
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            // Simulate API call
            kotlinx.coroutines.delay(1500)

            _state.update { it.copy(isLoading = false) }

            sendEffect(LoginEffect.NavigateToHome)
        }
    }

}