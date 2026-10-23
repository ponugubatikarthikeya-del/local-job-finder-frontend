package com.linker.app.core.repository

import androidx.paging.PagingData
import com.linker.app.core.data.models.HomeJobDto
import com.linker.app.core.data.models.JobDto
import com.linker.app.core.data.request.PostJobRequest
import com.linker.app.core.data.responses.JobDataResponse
import com.linker.app.core.data.responses.JobResponse
import com.linker.app.core.database.entity.JobEntity
import com.linker.app.core.network_api.NetworkResult
import kotlinx.coroutines.flow.Flow

interface JobRepository {
    suspend fun postJob(request: PostJobRequest): NetworkResult<JobDataResponse<JobDto>>
    suspend fun deleteJob(jobId: Long): NetworkResult<JobResponse>
    suspend fun completeJob(jobId: Long): NetworkResult<JobResponse>
    suspend fun applyJob(jobId: Long): NetworkResult<Unit>

    fun getJobsFlow(search: String?, category: String?, lat: Double, lng: Double): Flow<PagingData<HomeJobDto>>
    fun getMyJobsFlow(): Flow<PagingData<HomeJobDto>>
    suspend fun getJobById(jobId: Long): JobEntity?
    suspend fun clearJobs()
}
