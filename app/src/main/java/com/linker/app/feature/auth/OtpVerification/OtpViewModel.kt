package com.linker.app.feature.auth.OtpVerification

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.app.core.datastore.TokenManager
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.data.request.EmailCheckupRequest
import com.linker.app.core.data.request.ResetPasswordOtpValidateRequest
import com.linker.app.core.data.request.SignupOtpValidationRequest
import com.linker.app.core.repository.AuthRepository
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
class OtpViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var timerJob: kotlinx.coroutines.Job? = null
    private val _state = MutableStateFlow(OtpUiState())
    val state: StateFlow<OtpUiState> = _state.asStateFlow()

    private val _effect = Channel<OtpEffect>()
    val effect = _effect.receiveAsFlow()

    private var isResetFlow = false

    init {
        // Safely pull the email value directly out of the SavedStateHandle.
        val emailArg: String? = savedStateHandle["email"]
        emailArg?.let { email ->
            _state.update { it.copy(email = email) }
        }
        startTimer()
    }

    fun setFlowMode(isReset: Boolean) {
        this.isResetFlow = isReset
    }

    fun onEvent(event: OtpEvent) {
        when (event) {
            is OtpEvent.onEmailChanged -> {
                _state.update { it.copy(email = event.value) }
            }
            is OtpEvent.OnOtpChanged -> {
                if (event.value.length <= 6 && event.value.all { it.isDigit() }) {
                    _state.update { it.copy(otp = event.value) }
                }
            }
            is OtpEvent.OnVerifyClicked -> verifyOtp()
            is OtpEvent.OnResendClicked -> resendOtp()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _state.update { it.copy(timerTicks = 300) }
            while (_state.value.timerTicks > 0) {
                delay(1000.milliseconds)
                _state.update { it.copy(timerTicks = it.timerTicks - 1) }
            }
        }
    }

    private fun resendOtp() {
        if (_state.value.timerTicks == 0) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                val request = EmailCheckupRequest(email = _state.value.email)
                when (val result = repository.resendOtp(request)) {
                    is NetworkResult.Success -> {
                        _state.update { it.copy(isLoading = false) }
                        startTimer()
                        sendEffect(OtpEffect.ShowError("Code resent successfully"))
                    }
                    is NetworkResult.Failure -> {
                        _state.update { it.copy(isLoading = false) }
                        sendEffect(OtpEffect.ShowError(result.exception.message ?: "Failed to resend code"))
                    }
                    is NetworkResult.Loading -> {}
                }
            }
        }
    }

    private fun verifyOtp() {
        val currentState = _state.value
        if (currentState.otp.length < 6) {
            sendEffect(OtpEffect.ShowError("Please enter complete OTP"))
            return
        }

        if (isResetFlow) {
            performResetOtpVerification()
        } else {
            performSignupOtpVerification()
        }
    }

    private fun performSignupOtpVerification() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val currentOtp = _state.value.otp
            if (currentOtp.length != 6) {
                _state.update { it.copy(isLoading = false) }
                sendEffect(OtpEffect.ShowError("Invalid OTP length"))
                return@launch
            }

            val request = SignupOtpValidationRequest(
                email = _state.value.email,
                otp = currentOtp
            )

            when (val result = repository.verifyOtp(request)) {
                is NetworkResult.Success -> {
                    val dataMap = result.data.data
                    val accessToken = dataMap?.get("jwtAccessToken") ?: dataMap?.get("accessToken") ?: ""
                    val refreshToken = dataMap?.get("jwtRefreshToken") ?: dataMap?.get("refreshToken") ?: ""
                    tokenManager.saveTokens(accessToken, refreshToken)
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(OtpEffect.VerificationSuccess(currentOtp))
                }

                is NetworkResult.Failure -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.exception.message) }
                    sendEffect(OtpEffect.ShowError(result.exception.message ?: "Verification failed"))
                }

                is NetworkResult.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun performResetOtpVerification() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val currentOtp = _state.value.otp
            if (currentOtp.length != 6) {
                _state.update { it.copy(isLoading = false) }
                sendEffect(OtpEffect.ShowError("Invalid OTP length"))
                return@launch
            }

            val request = ResetPasswordOtpValidateRequest(
                email = _state.value.email,
                otp = currentOtp.toIntOrNull()
            )

            when (val result = repository.verifyResetOtp(request)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    tokenManager.saveResetKey(result.data.resetKey)
                    sendEffect(OtpEffect.VerificationSuccess(currentOtp))
                }
                is NetworkResult.Failure -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.exception.message) }
                    sendEffect(OtpEffect.ShowError(result.exception.message ?: "Verification failed"))
                }
                is NetworkResult.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun sendEffect(effect: OtpEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
