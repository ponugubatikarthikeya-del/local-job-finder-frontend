package com.linker.app.feature.screens.job.public_job_details

sealed interface PublicJobDetailsEffect {
    data object NavigateBack : PublicJobDetailsEffect
    data class ShowToast(val message: String) : PublicJobDetailsEffect
    data class NavigateToViewApplicants(val jobId: Long) : PublicJobDetailsEffect
    data class NavigateToProfile(val jobId: Long, val isPrivate: Boolean) : PublicJobDetailsEffect
}
