package com.linker.app.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.app.core.data.models.UserDto
import com.linker.app.core.datastore.TokenManager
import com.linker.app.core.network_api.AppException
import com.linker.app.core.network_api.AuthEventBus
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.network_api.connection.NetworkConnectivityObserver
import com.linker.app.core.repository.AuthRepository
import com.linker.app.core.repository.JobRepository
import com.linker.app.core.repository.ProfileRepository
import com.linker.app.main.splash.SplashState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository,
    private val jobRepository: JobRepository,
    private val profileRepository: ProfileRepository,
    private val authEventBus: AuthEventBus,
    networkConnectivityObserver: NetworkConnectivityObserver
) : ViewModel() {

    val userProfile: StateFlow<UserDto?> = profileRepository.getUserProfileFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _isStabilizing = MutableStateFlow(false)
    val isStabilizing = _isStabilizing.asStateFlow()

    private var stabilizationJob: kotlinx.coroutines.Job? = null

    // Real-time connectivity StateFlow with stabilization
    val isNetworkAvailable: StateFlow<Boolean> = networkConnectivityObserver.isConnected
        .onEach { isConnected ->
            stabilizationJob?.cancel()
            if (isConnected) {
                stabilizationJob = viewModelScope.launch {
                    _isStabilizing.value = true
                    delay(3000.milliseconds)
                    _isStabilizing.value = false
                }
            } else {
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
        observeAuthLogoutEvents()
    }

    private fun observeAuthLogoutEvents() {
        authEventBus.logoutEvent
            .onEach {
                _splashState.value = SplashState.Unauthenticated
            }
            .launchIn(viewModelScope)
    }

    fun checkAuthStatus() {
        viewModelScope.launch {
            val accessToken = tokenManager.accessToken.firstOrNull()
            val refreshToken = tokenManager.refreshToken.firstOrNull()

            if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
                _splashState.value = SplashState.Unauthenticated
                return@launch
            }

            if (!tokenManager.isTokenExpiredLocally(accessToken)) {
                _splashState.value = SplashState.Authenticated
                return@launch
            }

            performTokenRefresh(refreshToken)
        }
    }

    fun retryAuthCheck() {
        viewModelScope.launch {
            _splashState.value = SplashState.Loading
            delay(3000.milliseconds)

            val refreshToken = tokenManager.refreshToken.firstOrNull()
            performTokenRefresh(refreshToken)
        }
    }

    private suspend fun performTokenRefresh(refreshToken: String?) {
        if (refreshToken != null) {
            when (val result = authRepository.refreshTokens()) {
                is NetworkResult.Success -> {
                    tokenManager.saveTokens(
                        accessToken = result.data.data?.get("accessToken") ?: "",
                        refreshToken = result.data.data?.get("refreshToken") ?: ""
                    )
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

    fun logout(allDevices: Boolean = false) {
        viewModelScope.launch {
            val deviceId = tokenManager.getOrCreateDeviceId()
            authRepository.logout(deviceId, allDevices)
            tokenManager.clearAll()
            jobRepository.clearJobs()
            _splashState.value = SplashState.Unauthenticated
        }
    }
}
