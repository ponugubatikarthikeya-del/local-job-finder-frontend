package com.linker.app.core.repository

import com.linker.app.core.data.models.ApplicantDto
import com.linker.app.core.data.responses.JobResponse
import com.linker.app.core.data.responses.JobPagingResponse
import com.linker.app.core.data.responses.PagedResponse
import com.linker.app.core.network_api.NetworkResult
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface ApplicationsRepository {
    suspend fun applyJob(jobId: Long): NetworkResult<JobResponse>
    suspend fun viewApplicants(jobId: Long, page: Int = 0, size: Int = 20): NetworkResult<JobPagingResponse<PagedResponse<ApplicantDto>>>
    suspend fun syncApplicants(jobId: Long, page: Int = 0, size: Int = 20): NetworkResult<Unit>
    fun getApplicantsFlow(jobId: Long): Flow<PagingData<ApplicantDto>>
    suspend fun acceptApplicant(jobId: Long, applicantId: Long): NetworkResult<JobResponse>
    suspend fun rejectApplicant(jobId: Long, applicantId: Long): NetworkResult<JobResponse>
}
