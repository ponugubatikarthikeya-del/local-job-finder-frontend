package com.linker.app.core.network_api.repository

import com.linker.app.core.data.models.ApplicantDto
import com.linker.app.core.data.responses.JobResponse
import com.linker.app.core.data.responses.ViewApplicantsResponse
import com.linker.app.core.network_api.NetworkResult
import kotlinx.coroutines.flow.Flow

interface ApplicationsRepository {
    suspend fun applyJob(jobId: Long): NetworkResult<JobResponse>
    suspend fun viewApplicants(jobId: Long, page: Int = 0, size: Int = 20): NetworkResult<ViewApplicantsResponse>
    suspend fun syncApplicants(jobId: Long, page: Int = 0, size: Int = 20): NetworkResult<Unit>
    fun getApplicantsFlow(jobId: Long): Flow<List<ApplicantDto>>
    suspend fun acceptApplicant(jobId: Long, applicantId: Long): NetworkResult<JobResponse>
    suspend fun rejectApplicant(jobId: Long, applicantId: Long): NetworkResult<JobResponse>
}
