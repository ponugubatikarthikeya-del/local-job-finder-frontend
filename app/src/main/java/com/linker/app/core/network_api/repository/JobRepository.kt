package com.linker.app.core.network_api.repository

import com.linker.app.core.data.models.HomeJobCard
import com.linker.app.core.data.request.PostJobRequest
import com.linker.app.core.data.responses.UserResponse
import com.linker.app.core.database.entity.JobEntity
import com.linker.app.core.network_api.NetworkResult
import kotlinx.coroutines.flow.Flow

interface JobRepository {
    suspend fun getActiveJobsNearby(
        lat: Double,
        lng: Double,
        radius: Double = 20.0,
        page: Int = 0,
        size: Int = 20
    ): NetworkResult<Unit>

    suspend fun postJob(request: PostJobRequest): NetworkResult<UserResponse>
    suspend fun applyJob(jobId: Long): NetworkResult<Unit>
    suspend fun viewApplicants(jobId: Long, page: Int = 0, size: Int = 20): NetworkResult<com.linker.app.core.data.responses.ViewApplicantsResponse>
    suspend fun deleteJob(jobId: Long): NetworkResult<Unit>
    suspend fun getMyJobs(page: Int = 0, size: Int = 20): NetworkResult<Unit>
    suspend fun searchJobs(
        lat: Double,
        lng: Double,
        search: String? = null,
        category: String? = null,
        radius: Double = 100.0,
        page: Int = 0,
        size: Int = 20
    ): NetworkResult<Unit>

    suspend fun getJobs(): NetworkResult<List<HomeJobCard>>
    suspend fun syncJobs(): NetworkResult<Unit>
    fun getJobsFlow(): Flow<List<HomeJobCard>>
    fun getMyJobsFlow(): Flow<List<HomeJobCard>>
    suspend fun getJobById(jobId: Long): JobEntity?
    suspend fun clearJobs()
}
