package com.linker.app.feature.screens.job.my_jobs

import com.linker.app.core.data.models.HomeJobDto

sealed interface MyJobsEvent {
    data object OnRefresh : MyJobsEvent
    data class OnJobClicked(val job: HomeJobDto) : MyJobsEvent
    data object OnBackClicked : MyJobsEvent
}
