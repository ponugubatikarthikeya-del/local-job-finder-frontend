package com.linker.app.feature.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.data.request.SignupRequest
import com.linker.app.core.network_api.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

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
                sendEffect(SignupEffect.NavigateToOtpVerification(_state.value.email))
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
        val currentState = _state.value
        if (currentState.username.isBlank() || currentState.email.isBlank() || currentState.password.isBlank() || currentState.phone.isBlank()) {
            sendEffect(SignupEffect.ShowError("Please fill in all fields"))
            return
        }

        if(!currentState.email.contains("@gmail.com")){
            sendEffect(SignupEffect.ShowError("Please enter a valid email"))
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            delay(3000.milliseconds)

            val request = SignupRequest(
                username = currentState.username,
                phone = currentState.phone,
                email = currentState.email,
                password = currentState.password
            )

            when (val result = repository.signup(request)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(SignupEffect.NavigateToOtpVerification(currentState.email))
                }
                is NetworkResult.Failure -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.exception.message) }
                    sendEffect(SignupEffect.ShowError(result.exception.message ?: "Signup failed"))
                }
                is NetworkResult.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}
