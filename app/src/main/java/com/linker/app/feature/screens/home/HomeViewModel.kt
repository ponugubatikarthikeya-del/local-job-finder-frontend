package com.linker.app.feature.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.app.core.network_api.repository.JobRepository
import com.linker.app.core.datastore.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _effect = Channel<HomeEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var searchJob: kotlinx.coroutines.Job? = null
    private var refreshJob: kotlinx.coroutines.Job? = null
    private var isInitialSyncAttempted = false

    init {
        observeJobs()
        observeAddress()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnRefresh -> {
                if (refreshJob?.isActive != true) {
                    syncJobs()
                }
            }
            is HomeEvent.OnSearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
                if (event.query.isEmpty()) {
                    triggerFetchImmediately()
                }
            }
            is HomeEvent.OnSearchClicked -> {
                triggerFetchImmediately()
            }
            is HomeEvent.OnCategorySelected -> {
                val newCategory = if (_state.value.selectedCategory == event.category) null else event.category
                _state.update { it.copy(selectedCategory = newCategory) }
                triggerFetchImmediately()
            }
            is HomeEvent.OnJobClicked -> {
                viewModelScope.launch {
                    _effect.send(HomeEffect.NavigateToPublicCardDetails(event.job))
                }
            }
            is HomeEvent.OnProfileClicked -> {
                viewModelScope.launch {
                    _effect.send(HomeEffect.NavigateToPublicProfile(event.jobId))
                }
            }
            is HomeEvent.OnNotificationClicked -> {
                viewModelScope.launch {
                    _effect.send(HomeEffect.NavigateToNotification)
                }
            }
            is HomeEvent.OnMenuClicked -> {
                viewModelScope.launch {
                    _effect.send(HomeEffect.OpenDrawer)
                }
            }
        }
    }

    private fun triggerFetchImmediately() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            fetchJobs()
        }
    }

    private suspend fun fetchJobs() {
        val lat = tokenManager.latitude.first()?.toDoubleOrNull()
        val lng = tokenManager.longitude.first()?.toDoubleOrNull()

        if (lat != null && lng != null) {
            _state.update { it.copy(isLoading = true) }
            val query = _state.value.searchQuery.trim()
            val category = _state.value.selectedCategory

            if (query.isEmpty() && category == null) {
                // If filters are cleared, return to standard nearby jobs
                jobRepository.getActiveJobsNearby(lat, lng)
            } else {
                // Use search API for active filters
                jobRepository.searchJobs(
                    lat = lat,
                    lng = lng,
                    search = query.ifEmpty { null },
                    category = category
                )
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun observeJobs() {
        jobRepository.getJobsFlow()
            .onEach { jobs ->
                _state.update { it.copy(jobs = jobs) }
                
                // Fix for multiple calls: 
                // 1. Only sync if Room is empty 
                // 2. AND we haven't attempted a sync yet in this session
                // 3. AND we are not currently loading or filtering.
                if (jobs.isEmpty() && 
                    !isInitialSyncAttempted && 
                    !_state.value.isLoading && 
                    _state.value.searchQuery.isEmpty() && 
                    _state.value.selectedCategory == null) {
                    
                    isInitialSyncAttempted = true
                    syncJobs()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeAddress() {
        tokenManager.address
            .onEach { address ->
                _state.update { it.copy(address = address ?: "Select Location") }
            }
            .launchIn(viewModelScope)
    }

    private fun syncJobs() {
        if (refreshJob?.isActive == true) return
        refreshJob = viewModelScope.launch {
            fetchJobs()
        }
    }
}
