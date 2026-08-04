package com.linker.app.feature.screens.job.public_job_details

sealed interface PublicJobDetailsEvent {
    data object OnBackClicked : PublicJobDetailsEvent
    data object OnDeleteClicked : PublicJobDetailsEvent
    data object OnApplyClicked : PublicJobDetailsEvent
    data object OnViewApplicantsClicked : PublicJobDetailsEvent
    data object OnProfileClicked : PublicJobDetailsEvent
}
