package com.linker.app.feature.screens.job.my_jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.network_api.repository.JobRepository
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
import javax.inject.Inject

@HiltViewModel
class MyJobsViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MyJobsUiState())
    val state: StateFlow<MyJobsUiState> = _state.asStateFlow()

    private val _effect = Channel<MyJobsEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        observeMyJobs()
        fetchMyJobs()
    }

    private fun observeMyJobs() {
        jobRepository.getMyJobsFlow()
            .onEach { jobs ->
                _state.update { it.copy(jobs = jobs) }
            }
            .launchIn(viewModelScope)
    }

    private fun fetchMyJobs() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = jobRepository.getMyJobs()) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                }
                is NetworkResult.Failure -> {
                    _state.update { it.copy(isLoading = false, error = result.exception.message) }
                    _effect.send(MyJobsEffect.ShowToast(result.exception.message ?: "Failed to fetch jobs"))
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun onEvent(event: MyJobsEvent) {
        when (event) {
            is MyJobsEvent.OnRefresh -> fetchMyJobs()
            is MyJobsEvent.OnBackClicked -> {
                viewModelScope.launch { _effect.send(MyJobsEffect.NavigateBack) }
            }
            is MyJobsEvent.OnJobClicked -> {
                viewModelScope.launch { _effect.send(MyJobsEffect.NavigateToJobDetails(event.job)) }
            }
        }
    }
}
