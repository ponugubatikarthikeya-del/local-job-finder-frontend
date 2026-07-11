package com.linker.app.core.network.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ConnectivityViewModel @Inject constructor(
    networkConnectivityObserver: NetworkConnectivityObserver,
) : ViewModel() {

    val uiState: StateFlow<ConnectivityUiState> = networkConnectivityObserver.isConnected
        .map { ConnectivityUiState(isConnected = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ConnectivityUiState(isConnected = true)
        )
}