package com.linker.app.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.app.core.datastore.TokenManager
import com.linker.app.core.network_api.AppException
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.network_api.connection.NetworkConnectivityObserver
import com.linker.app.core.network_api.repository.AuthRepository
import com.linker.app.main.splash.SplashState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository,
    networkConnectivityObserver: NetworkConnectivityObserver
) : ViewModel() {

    private val _isStabilizing = MutableStateFlow(false)
    val isStabilizing = _isStabilizing.asStateFlow()

    // 2. Expose real-time connectivity as a StateFlow with stabilization logic
    val isNetworkAvailable: StateFlow<Boolean> = networkConnectivityObserver.isConnected
        .onEach { isConnected ->
            if (isConnected) {
                // When internet returns, trigger 3-second stabilization
                viewModelScope.launch {
                    _isStabilizing.value = true
                    delay(3000)
                    _isStabilizing.value = false
                }
            } else {
                // If internet is lost, stop stabilizing immediately
                _isStabilizing.value = false
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    private val _splashState = MutableStateFlow<SplashState>(SplashState.Idle)
    val splashState = _splashState.asStateFlow()

    private val _isSplashFinished = MutableStateFlow(false)
    val isSplashFinished = _isSplashFinished.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        viewModelScope.launch {
            // No longer setting _splashState.value = SplashState.Loading here
            // to avoid overriding the splash animation with a spinner on startup.
            
            val accessToken = tokenManager.accessToken.firstOrNull()
            val refreshToken = tokenManager.refreshToken.firstOrNull()

            // Strict Auth Rule: Both tokens MUST be present to even consider Authenticated state
            if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
                _splashState.value = SplashState.Unauthenticated
                return@launch
            }

            if (!tokenManager.isTokenExpiredLocally(accessToken)) {
                _splashState.value = SplashState.Authenticated
                return@launch
            }

            // Access token expired, try to refresh
            performTokenRefresh(refreshToken)
        }
    }

    fun retryAuthCheck() {
        viewModelScope.launch {
            _splashState.value = SplashState.Loading
            
            // Force 3-second delay as per requirement
            delay(3000)

            val refreshToken = tokenManager.refreshToken.firstOrNull()
            performTokenRefresh(refreshToken)
        }
    }

    private suspend fun performTokenRefresh(refreshToken: String?) {
        if (refreshToken != null) {
            when (val result = authRepository.refreshTokens()) {
                is NetworkResult.Success -> {
                    tokenManager.saveTokens(result.data.accessToken, result.data.refreshToken)
                    _splashState.value = SplashState.Authenticated
                }
                is NetworkResult.Failure -> {
                    when (result.exception) {
                        is AppException.ServerException -> {
                            tokenManager.clearTokens()
                            _splashState.value = SplashState.Unauthenticated
                        }
                        is AppException.NetworkException -> {
                            _splashState.value = SplashState.NetworkError("Server Unreachable")
                        }
                        else -> {
                            _splashState.value = SplashState.Unauthenticated
                        }
                    }
                }
                is NetworkResult.Loading -> {
                    _splashState.value = SplashState.Loading
                }
            }
        } else {
            _splashState.value = SplashState.Unauthenticated
        }
    }

    fun setReady(ready: Boolean) {
        _isReady.value = ready
    }

    fun splashFinished() {
        viewModelScope.launch {
            _isSplashFinished.value = true
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearTokens()
            _splashState.value = SplashState.Unauthenticated
        }
    }
}
