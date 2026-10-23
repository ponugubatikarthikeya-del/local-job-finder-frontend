package com.linker.app.feature.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.linker.app.core.datastore.TokenManager
import com.linker.app.core.repository.JobRepository
import com.linker.app.core.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val profileRepository: ProfileRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _effect = Channel<HomeEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    // Active search query - ONLY updated on explicit search submit (Arrow click / Enter) or when text is cleared
    private val _activeSearchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _locationParams = MutableStateFlow(Pair(0.0, 0.0))

    init {
        observeLocation()

        // Paging flow triggers API ONLY when active search query, category, or location parameters explicitly change
        val pagingFlow = combine(
            _activeSearchQuery,
            _selectedCategory,
            _locationParams
        ) { query, category, (lat, lng) ->
            SearchParams(
                query = query.trim().ifEmpty { null },
                category = category,
                lat = lat,
                lng = lng
            )
        }.distinctUntilChanged()
        .flatMapLatest { params ->
            jobRepository.getJobsFlow(
                search = params.query,
                category = params.category,
                lat = params.lat,
                lng = params.lng
            ).cachedIn(viewModelScope)
        }

        _state.update { it.copy(jobsPagingDataFlow = pagingFlow) }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnRefresh -> {
                // Handled natively by LazyPagingItems.refresh() in UI layer
            }
            is HomeEvent.OnSearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
                // If user clears the text box, reset active search query immediately
                if (event.query.isEmpty()) {
                    _activeSearchQuery.value = ""
                }
            }
            is HomeEvent.OnSearchClicked -> {
                // Execute API call ONLY when user clicks Search arrow or presses Enter on soft keyboard
                _activeSearchQuery.value = _state.value.searchQuery.trim()
            }
            is HomeEvent.OnCategorySelected -> {
                val newCategory = if (_state.value.selectedCategory == event.category) null else event.category
                _state.update { it.copy(selectedCategory = newCategory) }
                _selectedCategory.value = newCategory
            }
            is HomeEvent.OnJobClicked -> {
                viewModelScope.launch {
                    _effect.send(HomeEffect.NavigateToPublicCardDetails(event.job))
                }
            }
            is HomeEvent.OnProfileClicked -> {
                viewModelScope.launch {
                    val currentUserId = profileRepository.getUserProfileFlow().firstOrNull()?.id
                    if (currentUserId != null && event.job.userId == currentUserId) {
                        _effect.send(HomeEffect.NavigateToPrivateProfile)
                    } else {
                        _effect.send(HomeEffect.NavigateToPublicProfile(event.job.id))
                    }
                }
            }
            is HomeEvent.OnNotificationClicked -> {}
            is HomeEvent.OnMenuClicked -> {
                viewModelScope.launch {
                    _effect.send(HomeEffect.OpenDrawer)
                }
            }
            is HomeEvent.OnChangeLocationClicked -> {
                viewModelScope.launch {
                    _effect.send(HomeEffect.NavigateToChangeAddress)
                }
            }
        }
    }

    private fun observeLocation() {
        tokenManager.address
            .onEach { address ->
                _state.update { it.copy(address = address ?: "Select Location") }
            }
            .launchIn(viewModelScope)

        combine(
            tokenManager.latitude,
            tokenManager.longitude
        ) { latStr, lngStr ->
            val lat = latStr?.toDoubleOrNull() ?: 0.0
            val lng = lngStr?.toDoubleOrNull() ?: 0.0
            Pair(lat, lng)
        }.distinctUntilChanged()
        .onEach { coords ->
            if (coords.first != 0.0 || coords.second != 0.0) {
                _locationParams.value = coords
            }
        }.launchIn(viewModelScope)
    }

    private data class SearchParams(
        val query: String? = null,
        val category: String? = null,
        val lat: Double = 0.0,
        val lng: Double = 0.0
    )
}
