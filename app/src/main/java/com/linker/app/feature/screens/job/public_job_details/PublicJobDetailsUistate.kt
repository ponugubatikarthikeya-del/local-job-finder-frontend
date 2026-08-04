package com.linker.app.feature.screens.job.public_job_details

import com.linker.app.core.database.entity.JobEntity

data class PublicJobDetailsUistate(
    val job: JobEntity? = null,
    val showApplyButton: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null
)
