package com.linker.app.feature.screens.profile.public_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.app.core.data.models.UserDto
import com.linker.app.core.data.models.PublicProfileDto
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class PublicProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PublicProfileUistate())
    val state: StateFlow<PublicProfileUistate> = _state.asStateFlow()

    private val _effect = Channel<PublicProfileEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun loadProfile(jobId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = profileRepository.getPublicProfile(jobId)) {
                is NetworkResult.Success -> {
                    val publicProfileDto = result.data.data
                    if (publicProfileDto != null) {
                        _state.update { it.copy(user = publicProfileDto.toUser(), isLoading = false) }
                    } else {
                        _state.update { it.copy(isLoading = false, error = "Profile data is empty") }
                    }
                }
                is NetworkResult.Failure -> {
                    _state.update { it.copy(isLoading = false, error = result.exception.message) }
                }
                else -> {}
            }
        }
    }

    fun onEvent(event: PublicProfileEvent) {
        when (event) {
            is PublicProfileEvent.OnBackClicked -> {
                viewModelScope.launch {
                    _effect.send(PublicProfileEffect.NavigateBack)
                }
            }
        }
    }

    // Helper to convert PublicProfileDto to UserDto (Model)
    private fun PublicProfileDto.toUser() = UserDto(
        id = id,
        username = username,
        headline = headline,
        skills = skills ?: emptyList(),
        cityName = cityName,
        rating = rating ?: BigDecimal.ZERO,
        totalJobsCompleted = totalJobsCompleted ?: 0,
        userDescription = userDescription,
        email = null,
        phone = null,
        dateOfBirth = null,
        createdAt = Instant.now()
    )
}
