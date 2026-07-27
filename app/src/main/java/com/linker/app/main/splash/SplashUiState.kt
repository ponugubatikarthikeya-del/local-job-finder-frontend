package com.linker.app.main.splash

sealed class SplashState {
    data object Idle : SplashState()
    data object Loading : SplashState()
    data object Authenticated : SplashState()
    data object Unauthenticated : SplashState()
    data class NetworkError(val message: String) : SplashState()
}
