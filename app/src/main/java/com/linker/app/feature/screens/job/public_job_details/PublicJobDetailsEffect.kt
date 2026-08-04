package com.linker.app.feature.screens.job.public_job_details

sealed interface PublicJobDetailsEffect {
    data object NavigateBack : PublicJobDetailsEffect
}
