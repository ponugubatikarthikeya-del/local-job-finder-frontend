package com.linker.app.feature.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SignupUiState())
    val state: StateFlow<SignupUiState> = _state.asStateFlow()

    private val _effect = Channel<SignupEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: SignupEvent) {
        when (event) {
            is SignupEvent.OnUserNameChanged -> {
                _state.update { it.copy(username = event.value) }
            }
            is SignupEvent.OnPhoneChanged -> {
                _state.update { it.copy(phone = event.value) }
            }
            is SignupEvent.OnEmailChanged -> {
                _state.update { it.copy(email = event.value) }
            }
            is SignupEvent.OnPasswordChanged -> {
                _state.update { it.copy(password = event.value) }
            }
            is SignupEvent.OnTogglePasswordVisibility -> {
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            is SignupEvent.OnNavigateLoginClicked-> {
                sendEffect(SignupEffect.NavigateToLogin)
            }

            is SignupEvent.OnNavigateToOtpVerificationClicked -> {
                sendEffect(SignupEffect.NavigateToOtpVerification)
            }
            is SignupEvent.OnSignupBtnClicked -> {
                performSignup()
            }
            is SignupEvent.OnGoogleAuthClicked -> {
                // TODO: Google Auth logic
            }
            is SignupEvent.OnFacebookAuthClicked -> {
                // TODO: Facebook Auth logic
            }
        }
    }

    private fun sendEffect(effect: SignupEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    private fun performSignup() {
        if (_state.value.username.isBlank() || _state.value.email.isBlank() || _state.value.password.isBlank()) {
            sendEffect(SignupEffect.ShowError("Please fill in all fields"))
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            
            // Simulate API call
            kotlinx.coroutines.delay(1500)
            
            _state.update { it.copy(isLoading = false) }
            sendEffect(SignupEffect.SignupSuccess)
        }
    }
}
