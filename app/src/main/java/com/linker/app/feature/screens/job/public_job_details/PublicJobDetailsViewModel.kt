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

    fun loadJob(jobId: Long, showApplyButton: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, showApplyButton = showApplyButton) }
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
        }
    }
}
