package com.linker.app.feature.screens.job.public_job_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.app.core.network_api.repository.JobRepository
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
class PublicJobDetailsViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PublicJobDetailsUistate())
    val state: StateFlow<PublicJobDetailsUistate> = _state.asStateFlow()

    private val _effect = Channel<PublicJobDetailsEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun loadJob(jobId: Long, showApplyButton: Boolean, showDeleteButton: Boolean) {
        viewModelScope.launch {
            _state.update { 
                it.copy(
                    isLoading = true, 
                    showApplyButton = showApplyButton,
                    showDeleteButton = showDeleteButton
                ) 
            }
            val job = jobRepository.getJobById(jobId)
            _state.update { it.copy(job = job, isLoading = false) }
        }
    }

    fun onEvent(event: PublicJobDetailsEvent) {
        when (event) {
            is PublicJobDetailsEvent.OnBackClicked -> {
                viewModelScope.launch {
                    _effect.send(PublicJobDetailsEffect.NavigateBack)
                }
            }
            is PublicJobDetailsEvent.OnDeleteClicked -> {
                deleteJob()
            }
            is PublicJobDetailsEvent.OnApplyClicked -> {
                applyJob()
            }
            is PublicJobDetailsEvent.OnViewApplicantsClicked -> {
                viewModelScope.launch {
                    _state.value.job?.id?.let { jobId ->
                        _effect.send(PublicJobDetailsEffect.NavigateToViewApplicants(jobId))
                    }
                }
            }
            is PublicJobDetailsEvent.OnProfileClicked -> {
                viewModelScope.launch {
                    val job = _state.value.job ?: return@launch
                    val isPrivate = _state.value.showDeleteButton
                    _effect.send(PublicJobDetailsEffect.NavigateToProfile(job.id, isPrivate))
                }
            }
        }
    }

    private fun applyJob() {
        val jobId = _state.value.job?.id ?: return
        if (_state.value.isApplied) return // Safety check

        viewModelScope.launch {
            _state.update { it.copy(isApplying = true) }
            when (val result = jobRepository.applyJob(jobId)) {
                is com.linker.app.core.network_api.NetworkResult.Success -> {
                    _state.update { it.copy(isApplying = false, isApplied = true) }
                    _effect.send(PublicJobDetailsEffect.ShowToast("Applied successfully"))
                }
                is com.linker.app.core.network_api.NetworkResult.Failure -> {
                    _state.update { it.copy(isApplying = false) }
                    _effect.send(PublicJobDetailsEffect.ShowToast(result.exception.message ?: "Failed to apply"))
                }
                else -> {}
            }
        }
    }

    private fun deleteJob() {
        val jobId = _state.value.job?.id ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            when (val result = jobRepository.deleteJob(jobId)) {
                is com.linker.app.core.network_api.NetworkResult.Success -> {
                    _effect.send(PublicJobDetailsEffect.ShowToast("Job deleted successfully"))
                    _effect.send(PublicJobDetailsEffect.NavigateBack)
                }
                is com.linker.app.core.network_api.NetworkResult.Failure -> {
                    _state.update { it.copy(isDeleting = false) }
                    _effect.send(PublicJobDetailsEffect.ShowToast(result.exception.message ?: "Failed to delete job"))
                }
                else -> {}
            }
        }
    }
}
