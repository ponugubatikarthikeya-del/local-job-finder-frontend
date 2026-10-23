package com.linker.app.feature.screens.job.my_jobs

import com.linker.app.core.data.models.HomeJobDto

sealed interface MyJobsEffect {
    data object NavigateBack : MyJobsEffect
    data class NavigateToJobDetails(val job: HomeJobDto) : MyJobsEffect
    data class ShowToast(val message: String) : MyJobsEffect
}
