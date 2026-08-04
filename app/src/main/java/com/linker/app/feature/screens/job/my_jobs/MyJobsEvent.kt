package com.linker.app.feature.screens.job.my_jobs

import com.linker.app.core.data.models.HomeJobCard

sealed interface MyJobsEvent {
    data object OnRefresh : MyJobsEvent
    data class OnJobClicked(val job: HomeJobCard) : MyJobsEvent
    data object OnBackClicked : MyJobsEvent
}
