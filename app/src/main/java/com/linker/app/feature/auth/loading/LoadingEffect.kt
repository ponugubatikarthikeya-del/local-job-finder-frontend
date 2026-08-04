package com.linker.app.feature.auth.loading

sealed interface LoadingEffect {

    data object NavigateToHome : LoadingEffect
    data object NavigateToDetailsTake : LoadingEffect

    data class ShowError(val message : String) : LoadingEffect
}