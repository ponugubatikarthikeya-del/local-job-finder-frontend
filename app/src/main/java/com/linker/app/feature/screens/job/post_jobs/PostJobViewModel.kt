package com.linker.app.feature.screens.job.post_jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.app.core.data.request.PostJobRequest
import com.linker.app.core.datastore.TokenManager
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.repository.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class PostJobViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(PostJobUiState())
    val state: StateFlow<PostJobUiState> = _state.asStateFlow()

    private val _effect = Channel<PostJobEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        initializeLocation()
    }

    private fun initializeLocation() {
        viewModelScope.launch {
            val lat = tokenManager.latitude.first()?.toDoubleOrNull()
            val lng = tokenManager.longitude.first()?.toDoubleOrNull()
            val addr = tokenManager.address.first() ?: ""
            _state.update { it.copy(latitude = lat, longitude = lng, address = addr) }
        }
    }

    fun onEvent(event: PostJobEvent) {
        when (event) {
            is PostJobEvent.OnTitleChanged -> _state.update { it.copy(title = event.value) }
            is PostJobEvent.OnDescriptionChanged -> _state.update { it.copy(description = event.value) }
            is PostJobEvent.OnCategoryChanged -> _state.update { it.copy(category = event.value) }
            is PostJobEvent.OnPayAmountChanged -> _state.update { it.copy(payAmount = event.value) }
            is PostJobEvent.OnJobTypeChanged -> _state.update { it.copy(jobType = event.value) }
            is PostJobEvent.OnAddressChanged -> _state.update { it.copy(address = event.value) }
            is PostJobEvent.OnJobTimeChanged -> _state.update { it.copy(jobTime = event.value) }
            is PostJobEvent.OnPostClicked -> postJob()
            is PostJobEvent.OnBackClicked -> {
                viewModelScope.launch {
                    _effect.send(PostJobEffect.NavigateBack)
                }
            }
        }
    }

    private fun postJob() {
        val s = _state.value

        if (s.title.isBlank() || s.description.isBlank() || s.category.isBlank() || s.payAmount.isBlank()) {
            viewModelScope.launch { _effect.send(PostJobEffect.ShowSuccessToast("Please fill all required fields")) }
            return
        }

        val pay = s.payAmount.toBigDecimalOrNull() ?: run {
            viewModelScope.launch { _effect.send(PostJobEffect.ShowSuccessToast("Invalid pay amount")) }
            return
        }

        if (s.latitude == null || s.longitude == null) {
            viewModelScope.launch { _effect.send(PostJobEffect.ShowSuccessToast("Location not found")) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val request = PostJobRequest(
                title = s.title,
                description = s.description,
                category = s.category,
                payAmount = pay,
                jobType = s.jobType,
                latitude = BigDecimal.valueOf(s.latitude),
                longitude = BigDecimal.valueOf(s.longitude),
                address = s.address,
                jobTime = s.jobTime
            )

            when (val result = jobRepository.postJob(request)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(PostJobEffect.ShowSuccessToast("Job posted successfully!"))
                    _effect.send(PostJobEffect.NavigateBack)
                }
                is NetworkResult.Failure -> {
                    _state.update { it.copy(isLoading = false, error = result.exception.message) }
                    _effect.send(PostJobEffect.ShowSuccessToast(result.exception.message ?: "Failed to post job"))
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
