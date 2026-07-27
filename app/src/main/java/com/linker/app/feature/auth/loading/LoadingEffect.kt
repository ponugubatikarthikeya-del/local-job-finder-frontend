package com.linker.app.feature.auth.loading

sealed interface LoadingEffect {

    object onNavigateToHome : LoadingEffect

    data class ShowError(val message : String) : LoadingEffect
}