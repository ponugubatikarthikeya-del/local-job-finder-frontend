package com.linker.app.feature.auth.reset_password.resetEmailEvent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.app.core.data.request.EmailCheckupRequest
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.network_api.repository.AuthRepository
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
class ResetEmailViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

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
        val email = _state.value.email
        if (email.isBlank()) {
            sendEffect(ResetEmailEffect.ShowError("Please enter your email"))
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val request = EmailCheckupRequest(email = email)
            when (val result = authRepository.forgotPassword(request)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(ResetEmailEffect.SendSuccess(email))
                }
                is NetworkResult.Failure -> {
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(ResetEmailEffect.ShowError(result.exception.message ?: "Request failed"))
                }
                is NetworkResult.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun sendEffect(effect: ResetEmailEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
