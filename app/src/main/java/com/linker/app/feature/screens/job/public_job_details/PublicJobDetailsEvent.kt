package com.linker.app.feature.screens.job.public_job_details

sealed interface PublicJobDetailsEvent {
    data object OnBackClicked : PublicJobDetailsEvent
}
