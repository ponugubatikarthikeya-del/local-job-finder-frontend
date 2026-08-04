package com.linker.app.feature.screens.job.post_jobs

sealed interface PostJobEffect {
    data object NavigateBack : PostJobEffect
    data class ShowSuccessToast(val message: String) : PostJobEffect
    data class ShowError(val message: String) : PostJobEffect
}
