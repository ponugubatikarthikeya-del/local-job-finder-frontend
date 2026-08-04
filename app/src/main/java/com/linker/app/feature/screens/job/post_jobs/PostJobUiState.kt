package com.linker.app.feature.screens.job.post_jobs

import com.linker.app.core.util.enums.JobTypeEnum
import java.time.Instant

data class PostJobUiState(
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val payAmount: String = "",
    val jobType: JobTypeEnum = JobTypeEnum.FIXED,
    val address: String = "",
    val jobTime: Instant = Instant.now(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
