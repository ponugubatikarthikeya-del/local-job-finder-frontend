package com.linker.app.feature.screens.profile.profile_update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.app.core.data.request.EditProfileRequest
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UpdateProfileUiState())
    val state: StateFlow<UpdateProfileUiState> = _state.asStateFlow()

    private val _effect = Channel<UpdateProfileEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val user = profileRepository.getUserProfileFlow().first()
            user?.let { u ->
                _state.update {
                    it.copy(
                        username = u.username,
                        phone = u.phone ?: "",
                        headline = u.headline ?: "",
                        dateOfBirth = u.dateOfBirth ?: "",
                        skills = u.skills?.joinToString(", ") ?: "",
                        cityName = u.cityName ?: "",
                        userDescription = u.userDescription ?: "",
                        isLoading = false
                    )
                }
            } ?: run {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onEvent(event: UpdateProfileEvent) {
        when (event) {
            is UpdateProfileEvent.OnUsernameChanged -> _state.update { it.copy(username = event.value) }
            is UpdateProfileEvent.OnPhoneChanged -> _state.update { it.copy(phone = event.value) }
            is UpdateProfileEvent.OnHeadlineChanged -> _state.update { it.copy(headline = event.value) }
            is UpdateProfileEvent.OnDobChanged -> _state.update { it.copy(dateOfBirth = event.value) }
            is UpdateProfileEvent.OnSkillsChanged -> _state.update { it.copy(skills = event.value) }
            is UpdateProfileEvent.OnCityChanged -> _state.update { it.copy(cityName = event.value) }
            is UpdateProfileEvent.OnBioChanged -> _state.update { it.copy(userDescription = event.value) }
            is UpdateProfileEvent.OnBackClicked -> {
                viewModelScope.launch { _effect.send(UpdateProfileEffect.NavigateBack) }
            }
            is UpdateProfileEvent.OnSaveClicked -> {
                saveProfile()
            }
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val s = _state.value
            val request = EditProfileRequest(
                username = s.username,
                phone = s.phone,
                headline = s.headline,
                dateOfBirth = s.dateOfBirth,
                skills = s.skills.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                cityName = s.cityName,
                userDescription = s.userDescription
            )
            
            when (val result = profileRepository.updateProfile(request)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isSaving = false) }
                    _effect.send(UpdateProfileEffect.ShowToast("Profile updated successfully!"))
                    _effect.send(UpdateProfileEffect.NavigateBack)
                }
                is NetworkResult.Failure -> {
                    _state.update { it.copy(isSaving = false, error = result.exception.message) }
                    _effect.send(UpdateProfileEffect.ShowToast(result.exception.message ?: "Failed to update profile"))
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
