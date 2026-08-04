package com.linker.app.core.repository_impl

import com.linker.app.core.data.responses.JobResponse
import com.linker.app.core.data.responses.ViewApplicantsResponse
import com.linker.app.core.network_api.AppException
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.network_api.api.ApplicationsApiService
import com.linker.app.core.network_api.repository.ApplicationsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ApplicationsRepositoryImpl @Inject constructor(
    private val apiService: ApplicationsApiService,
    private val ioDispatcher: CoroutineDispatcher
) : ApplicationsRepository {

    override suspend fun applyJob(jobId: Long): NetworkResult<JobResponse> = withContext(ioDispatcher) {
        try {
            val response = apiService.applyJob(jobId)
            NetworkResult.Success(response)
        } catch (e: Exception) {
            NetworkResult.Failure(AppException.NetworkException(e.message ?: "Apply job failed", e))
        }
    }

    override suspend fun viewApplicants(jobId: Long, page: Int, size: Int): NetworkResult<ViewApplicantsResponse> = withContext(ioDispatcher) {
        try {
            val response = apiService.viewApplicants(jobId, page, size)
            NetworkResult.Success(response)
        } catch (e: Exception) {
            NetworkResult.Failure(AppException.NetworkException(e.message ?: "Fetch applicants failed", e))
        }
    }

    override suspend fun acceptApplicant(jobId: Long, applicantId: Long): NetworkResult<JobResponse> = withContext(ioDispatcher) {
        try {
            val response = apiService.acceptApplicant(jobId, applicantId)
            NetworkResult.Success(response)
        } catch (e: Exception) {
            NetworkResult.Failure(AppException.NetworkException(e.message ?: "Accept applicant failed", e))
        }
    }

    override suspend fun rejectApplicant(jobId: Long, applicantId: Long): NetworkResult<JobResponse> = withContext(ioDispatcher) {
        try {
            val response = apiService.rejectApplicant(jobId, applicantId)
            NetworkResult.Success(response)
        } catch (e: Exception) {
            NetworkResult.Failure(AppException.NetworkException(e.message ?: "Reject applicant failed", e))
        }
    }
}
