package com.linker.app.feature.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.app.core.network_api.repository.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        observeJobs()
    }

    private fun observeJobs() {
        jobRepository.getJobsFlow()
            .onEach { jobs ->
                _state.update { it.copy(jobs = jobs) }
                if (jobs.isEmpty()) {
                    syncJobs()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun syncJobs() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            jobRepository.syncJobs()
            _state.update { it.copy(isLoading = false) }
        }
    }
}
