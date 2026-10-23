package com.linker.app.feature.screens.job.my_jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.repository.JobRepository
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
        _state.update { 
            it.copy(
                jobsPagingDataFlow = jobRepository.getMyJobsFlow().cachedIn(viewModelScope)
            ) 
        }
    }

    fun onEvent(event: MyJobsEvent) {
        when (event) {
            is MyJobsEvent.OnRefresh -> {
                // handled by UI Layer using paging item refresh
            }

            is MyJobsEvent.OnBackClicked -> {
                viewModelScope.launch { _effect.send(MyJobsEffect.NavigateBack) }
            }

            is MyJobsEvent.OnJobClicked -> {
                viewModelScope.launch { _effect.send(MyJobsEffect.NavigateToJobDetails(event.job)) }
            }
        }
    }
}
