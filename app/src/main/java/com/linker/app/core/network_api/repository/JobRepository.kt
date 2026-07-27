package com.linker.app.core.network_api.repository

import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.data.models.JobCard
import kotlinx.coroutines.flow.Flow

interface JobRepository {
    suspend fun getJobs(): NetworkResult<List<JobCard>>
    suspend fun syncJobs(): NetworkResult<Unit>
    fun getJobsFlow(): Flow<List<JobCard>>
    suspend fun clearJobs()
}
