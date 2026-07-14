package com.linker.app.feature.auth.reset_password.resetEmailEvent

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
class ResetEmailViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ResetEmailUiState())
    val state: StateFlow<ResetEmailUiState> = _state.asStateFlow()

    private val _effect = Channel<ResetEmailEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: ResetEmailEvent) {
        when (event) {
            is ResetEmailEvent.OnEmailChanged -> {
                _state.update { it.copy(email = event.value) }
            }
            is ResetEmailEvent.OnSendClicked -> sendResetEmail()
        }
    }

    private fun sendResetEmail() {
        if (_state.value.email.isBlank()) {
            sendEffect(ResetEmailEffect.ShowError("Please enter your email"))
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(1500) // Simulate API
            _state.update { it.copy(isLoading = false) }
            sendEffect(ResetEmailEffect.SendSuccess)
        }
    }

    private fun sendEffect(effect: ResetEmailEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
