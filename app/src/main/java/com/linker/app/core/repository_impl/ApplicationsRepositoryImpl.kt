package com.linker.app.core.repository_impl

import com.linker.app.core.data.models.ApplicantDto
import com.linker.app.core.data.responses.JobResponse
import com.linker.app.core.data.responses.ViewApplicantsResponse
import com.linker.app.core.database.dao.ApplicantDao
import com.linker.app.core.database.entity.ApplicantEntity
import com.linker.app.core.network_api.AppException
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.network_api.api.ApplicationsApiService
import com.linker.app.core.network_api.repository.ApplicationsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ApplicationsRepositoryImpl @Inject constructor(
    private val apiService: ApplicationsApiService,
    private val applicantDao: ApplicantDao,
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

    override suspend fun syncApplicants(jobId: Long, page: Int, size: Int): NetworkResult<Unit> = withContext(ioDispatcher) {
        try {
            val response = apiService.viewApplicants(jobId, page, size)
            val entities = response.allJobs?.map { it.toEntity(jobId) } ?: emptyList()
            // 🟢 Removed deleteApplicantsForJob to preserve locally updated states
            applicantDao.upsertApplicants(entities)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Failure(AppException.NetworkException(e.message ?: "Sync applicants failed", e))
        }
    }

    override fun getApplicantsFlow(jobId: Long): Flow<List<ApplicantDto>> {
        return applicantDao.getApplicantsForJob(jobId).map { entities ->
            entities.map { it.toDto() }
        }
    }

    override suspend fun acceptApplicant(jobId: Long, applicantId: Long): NetworkResult<JobResponse> = withContext(ioDispatcher) {
        try {
            val response = apiService.acceptApplicant(jobId, applicantId)
            applicantDao.updateApplicantStatus(applicantId, "ACCEPTED")
            NetworkResult.Success(response)
        } catch (e: Exception) {
            NetworkResult.Failure(AppException.NetworkException(e.message ?: "Accept applicant failed", e))
        }
    }

    override suspend fun rejectApplicant(jobId: Long, applicantId: Long): NetworkResult<JobResponse> = withContext(ioDispatcher) {
        try {
            val response = apiService.rejectApplicant(jobId, applicantId)
            applicantDao.updateApplicantStatus(applicantId, "REJECTED")
            NetworkResult.Success(response)
        } catch (e: Exception) {
            NetworkResult.Failure(AppException.NetworkException(e.message ?: "Reject applicant failed", e))
        }
    }

    private fun ApplicantDto.toEntity(jobId: Long) = ApplicantEntity(
        applicantId = displayApplicantId,
        jobId = jobId,
        userId = userId,
        username = username,
        headline = headline,
        status = status ?: "PENDING",
        appliedAt = appliedAt
    )

    private fun ApplicantEntity.toDto() = ApplicantDto(
        id = applicantId,
        userId = userId,
        username = username,
        headline = headline,
        status = status,
        appliedAt = appliedAt
    )
}
