package com.linker.app.feature.auth.reset_password.resetPassword

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
class ResetPasswordViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ResetPasswordUiState())
    val state: StateFlow<ResetPasswordUiState> = _state.asStateFlow()

    private val _effect = Channel<ResetPasswordEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: ResetPasswordEvent) {
        when (event) {
            is ResetPasswordEvent.OnPasswordChanged -> {
                _state.update { it.copy(password = event.value) }
            }
            is ResetPasswordEvent.OnConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPassword = event.value) }
            }
            is ResetPasswordEvent.OnTogglePasswordVisibility -> {
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            is ResetPasswordEvent.OnResetClicked -> resetPassword()
        }
    }

    private fun resetPassword() {
        val currentPassword = _state.value.password
        val currentConfirm = _state.value.confirmPassword

        if (currentPassword.isBlank() || currentConfirm.isBlank()) {
            sendEffect(ResetPasswordEffect.ShowError("Please fill in both fields"))
            return
        }

        if (currentPassword != currentConfirm) {
            sendEffect(ResetPasswordEffect.ShowError("Passwords do not match"))
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(1500) // Simulate API
            _state.update { it.copy(isLoading = false) }
            sendEffect(ResetPasswordEffect.ResetSuccess)
        }
    }

    private fun sendEffect(effect: ResetPasswordEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
