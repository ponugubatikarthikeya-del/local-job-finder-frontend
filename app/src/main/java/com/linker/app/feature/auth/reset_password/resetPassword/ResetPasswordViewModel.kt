package com.linker.app.feature.auth.reset_password.resetPassword

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.app.core.data.request.PasswordRequest
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.repository.AuthRepository
import com.linker.app.core.datastore.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(ResetPasswordUiState())
    val state: StateFlow<ResetPasswordUiState> = _state.asStateFlow()

    private val _effect = Channel<ResetPasswordEffect>()
    val effect = _effect.receiveAsFlow()

    private var email: String = ""
    private var otp: Int = 0

    init {
        // Retrieve arguments directly from SavedStateHandle to avoid navigation casting issues.
        email = savedStateHandle["email"] ?: ""
        otp = savedStateHandle["otp"] ?: 0
    }

    // Expose resetKey as a StateFlow
    val resetKey: StateFlow<String?> = tokenManager.resetKey
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

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

            val currentResetKey = resetKey.value

            if(currentResetKey.isNullOrBlank()){
                sendEffect(ResetPasswordEffect.ShowError("Invalid reset key"))
                return@launch
            }

            val request = PasswordRequest(
                resetKey = currentResetKey, // Using resetKey as per backend requirements
                resetPassword = currentPassword,
                confirmPassword = currentConfirm
            )

            when (val result = authRepository.changePassword(request)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    tokenManager.clearResetKey()
                    sendEffect(ResetPasswordEffect.ResetSuccess)
                }
                is NetworkResult.Failure -> {
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(ResetPasswordEffect.ShowError(result.exception.message ?: "Reset failed"))
                }
                is NetworkResult.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun sendEffect(effect: ResetPasswordEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
