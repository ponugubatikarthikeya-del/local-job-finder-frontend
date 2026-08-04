package com.linker.app.core.data.responses

import java.time.Instant

data class AllJobsResponse(
    val timestamp: Instant,
    val status: Int,
    val errorCode: String? = null,
    val message: String? = null,
    val path: String? = null,
    val allJobs: List<JobDto>
)
