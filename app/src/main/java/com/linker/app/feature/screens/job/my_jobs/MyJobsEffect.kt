package com.linker.app.feature.screens.job.my_jobs

import com.linker.app.core.data.models.HomeJobCard

sealed interface MyJobsEffect {
    data object NavigateBack : MyJobsEffect
    data class NavigateToJobDetails(val job: HomeJobCard) : MyJobsEffect
    data class ShowToast(val message: String) : MyJobsEffect
}
