package com.linker.app.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.app.core.datastore.TokenManager
import com.linker.app.core.network_api.AppException
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.data.request.EmailCheckupRequest
import com.linker.app.core.data.request.LoginRequest
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
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel (){

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
                sendEffect(LoginEffect.NavigateToOtpVerification(_state.value.email))
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
        val email = _state.value.email
        val password = _state.value.password

        if (email.isBlank() || password.isBlank()) {
            sendEffect(LoginEffect.ShowError("please fill the blanks"))
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            delay(3000.milliseconds)

            val request = LoginRequest(email = email, password = password)

            when (val result = repository.login(request)) {
                is NetworkResult.Success -> {
                    tokenManager.saveTokens(
                        accessToken = result.data.accessToken,
                        refreshToken = result.data.refreshToken
                    )
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(LoginEffect.NavigateToLoadingScreen)
                }
                is NetworkResult.Failure -> {
                    val exception = result.exception
                    // Check if user is not verified
                    if (exception is AppException.ServerException && exception.message?.contains("Account not verified.", ignoreCase = true) == true) {
                        // Automatically trigger resend OTP and navigate
                        when (val resendResult = repository.resendOtp(EmailCheckupRequest(email = email))) {
                            is NetworkResult.Success -> {
                                _state.update { it.copy(isLoading = false) }
                                sendEffect(LoginEffect.NavigateToOtpVerification(email))
                            }
                            is NetworkResult.Failure -> {
                                _state.update { it.copy(isLoading = false) }
                                sendEffect(LoginEffect.ShowError(resendResult.exception.message ?: "Failed to resend verification code"))
                            }
                            is NetworkResult.Loading -> {}
                        }
                    } else {
                        _state.update { it.copy(isLoading = false) }
                        sendEffect(LoginEffect.ShowError(exception.message ?: "Login failed"))
                    }
                }
                is NetworkResult.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

}