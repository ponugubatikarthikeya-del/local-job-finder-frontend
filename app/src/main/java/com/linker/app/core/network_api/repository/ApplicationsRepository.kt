package com.linker.app.core.network_api.repository

import com.linker.app.core.data.responses.JobResponse
import com.linker.app.core.data.responses.ViewApplicantsResponse
import com.linker.app.core.network_api.NetworkResult

interface ApplicationsRepository {
    suspend fun applyJob(jobId: Long): NetworkResult<JobResponse>
    suspend fun viewApplicants(jobId: Long, page: Int = 0, size: Int = 20): NetworkResult<ViewApplicantsResponse>
    suspend fun acceptApplicant(jobId: Long, applicantId: Long): NetworkResult<JobResponse>
    suspend fun rejectApplicant(jobId: Long, applicantId: Long): NetworkResult<JobResponse>
}
