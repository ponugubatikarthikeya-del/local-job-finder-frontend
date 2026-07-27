package com.linker.app.feature.auth.loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.app.core.network_api.repository.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel (){

    private val _state = MutableStateFlow(LoadingUiState())
    val state : StateFlow<LoadingUiState> = _state.asStateFlow()

    private val _effect = Channel<LoadingEffect>()
    val effect = _effect.receiveAsFlow()

    private val quotes = listOf(
        "Your hard work will lead you to success.",
        "The best way to predict the future is to create it.",
        "Opportunities don't happen, you create them.",
        "Success is not final, failure is not fatal: it is the courage to continue that counts.",
        "Believe you can and you're halfway there."
    )

    private fun sendEffect(effect : LoadingEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    init {
        performLoading()
    }

    private fun performLoading() {
        val randomQuote = quotes.random()
        _state.update { it.copy(isLoading = true, quote = randomQuote) }

        viewModelScope.launch {
            // Start fetching jobs in parallel and store in Room
            launch {
                jobRepository.syncJobs()
            }

            // Minimum 3 seconds delay
            delay(3000)
            
            // Wait for sync to complete if it hasn't already (optional, but good for data consistency)
            // jobsSyncJob.join() 
            
            _state.update { it.copy(isLoading = false) }
            sendEffect(LoadingEffect.onNavigateToHome)
        }
    }
}
