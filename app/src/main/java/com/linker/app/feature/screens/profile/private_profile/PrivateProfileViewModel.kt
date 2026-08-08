package com.linker.app.feature.screens.profile.private_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.app.core.data.models.User
import com.linker.app.core.datastore.TokenManager
import com.linker.app.core.network_api.repository.AuthRepository
import com.linker.app.core.network_api.repository.JobRepository
import com.linker.app.core.network_api.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class PrivateProfileViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val jobRepository: JobRepository,
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PrivateProfileUiState())
    val state: StateFlow<PrivateProfileUiState> = _state.asStateFlow()

    private val _effect = Channel<PrivateProfileEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        observeUserProfile()
    }

    private fun observeUserProfile() {
        profileRepository.getUserProfileFlow()
            .onEach { user ->
                _state.update { it.copy(user = user) }
                // Only fetch from API if we don't have user data locally
                if (user == null && !_state.value.isLoading) {
                    fetchUserProfile()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            profileRepository.getMyProfile()
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onEvent(event: PrivateProfileEvent) {
        when (event) {
            is PrivateProfileEvent.OnRefresh -> {
                fetchUserProfile()
            }
            is PrivateProfileEvent.OnBackClicked -> {
                viewModelScope.launch { _effect.send(PrivateProfileEffect.NavigateBack) }
            }
            is PrivateProfileEvent.OnEditProfileClicked -> {
                viewModelScope.launch { _effect.send(PrivateProfileEffect.NavigateToEditProfile) }
            }
            is PrivateProfileEvent.OnViewPublicProfileClicked -> {
                viewModelScope.launch { _effect.send(PrivateProfileEffect.NavigateToPublicProfile) }
            }
            is PrivateProfileEvent.OnLogoutClicked -> {
                logout(allDevices = true)
            }
        }
    }

    private fun logout(allDevices: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val deviceId = tokenManager.getOrCreateDeviceId()
            val fcmToken = try {
                com.google.firebase.messaging.FirebaseMessaging.getInstance().token.await()
            } catch (_: Exception) {
                ""
            }
            authRepository.logout(deviceId, allDevices, fcmToken)
            tokenManager.clearAll()
            jobRepository.clearJobs()
            profileRepository.clearProfile()
            _state.update { it.copy(isLoading = false) }
            _effect.send(PrivateProfileEffect.OnLogoutComplete)
        }
    }
}
