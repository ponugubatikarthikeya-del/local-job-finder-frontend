package com.linker.app.feature.auth.OtpVerification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

@HiltViewModel
class OtpViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(OtpUiState())
    val state: StateFlow<OtpUiState> = _state.asStateFlow()

    private val _effect = Channel<OtpEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        startTimer()
    }

    fun onEvent(event: OtpEvent) {
        when (event) {
            is OtpEvent.OnOtpChanged -> {
                if (event.value.length <= 4) {
                    _state.update { it.copy(otp = event.value) }
                }
            }
            is OtpEvent.OnVerifyClicked -> verifyOtp()
            is OtpEvent.OnResendClicked -> resendOtp()
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            _state.update { it.copy(timerTicks = 30) }
            while (_state.value.timerTicks > 0) {
                delay(1000)
                _state.update { it.copy(timerTicks = it.timerTicks - 1) }
            }
        }
    }

    private fun resendOtp() {
        if (_state.value.timerTicks == 0) {
            startTimer()
            // Logic to resend OTP via API
        }
    }

    private fun verifyOtp() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(1500) // Simulate API
            _state.update { it.copy(isLoading = false) }
            
            if (_state.value.otp == "1234") { // Mock success
                _effect.send(OtpEffect.VerificationSuccess)
            } else {
                _effect.send(OtpEffect.ShowError("Invalid OTP code"))
            }
        }
    }
}
