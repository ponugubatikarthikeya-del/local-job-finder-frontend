package com.linker.app.core.repository_impl

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.linker.app.core.data.models.ApplicantDto
import com.linker.app.core.data.responses.JobPagingResponse
import com.linker.app.core.data.responses.JobResponse
import com.linker.app.core.data.responses.PagedResponse
import com.linker.app.core.database.app_database.JobDatabase
import com.linker.app.core.database.entity.ApplicantEntity
import com.linker.app.core.database.paging.ApplicationPageMediator
import com.linker.app.core.network_api.AppException
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.network_api.api.ApplicationsApiService
import com.linker.app.core.repository.ApplicationsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ApplicationsRepositoryImpl @Inject constructor(
    private val apiService: ApplicationsApiService,
    private val database: JobDatabase,
    private val ioDispatcher: CoroutineDispatcher
) : ApplicationsRepository {

    private val applicantDao = database.applicantDao()

    override suspend fun applyJob(jobId: Long): NetworkResult<JobResponse> = withContext(ioDispatcher) {
        try {
            val response = apiService.applyJob(jobId)
            NetworkResult.Success(response)
        } catch (e: Exception) {
            NetworkResult.Failure(AppException.NetworkException(e.message ?: "Apply job failed", e))
        }
    }

    override suspend fun viewApplicants(jobId: Long, page: Int, size: Int): NetworkResult<JobPagingResponse<PagedResponse<ApplicantDto>>> = withContext(ioDispatcher) {
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
            val entities = response.data.content.map { it.toEntity(jobId) }
            applicantDao.upsertApplicants(entities)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Failure(AppException.NetworkException(e.message ?: "Sync applicants failed", e))
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getApplicantsFlow(jobId: Long): Flow<PagingData<ApplicantDto>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = ApplicationPageMediator(
                jobId = jobId,
                apiService = apiService,
                database = database
            ),
            pagingSourceFactory = { applicantDao.getApplicantsForJob(jobId) }
        ).flow.map { pagingData ->
            pagingData.map { it.toDto() }
        }
    }

    override suspend fun acceptApplicant(jobId: Long, applicantId: Long): NetworkResult<JobResponse> = withContext(ioDispatcher) {
        try {
            val response = apiService.acceptApplicant(jobId, applicantId)
            applicantDao.updateApplicantStatus(jobId, applicantId, "ACCEPTED")
            NetworkResult.Success(response)
        } catch (e: Exception) {
            NetworkResult.Failure(AppException.NetworkException(e.message ?: "Accept applicant failed", e))
        }
    }

    override suspend fun rejectApplicant(jobId: Long, applicantId: Long): NetworkResult<JobResponse> = withContext(ioDispatcher) {
        try {
            val response = apiService.rejectApplicant(jobId, applicantId)
            applicantDao.updateApplicantStatus(jobId, applicantId, "REJECTED")
            NetworkResult.Success(response)
        } catch (e: Exception) {
            NetworkResult.Failure(AppException.NetworkException(e.message ?: "Reject applicant failed", e))
        }
    }

    private fun ApplicantDto.toEntity(jobId: Long) = ApplicantEntity(
        applicantId = uniqueRoomKey,
        applicationId = applicationId,
        id = id,
        backendApplicantId = backendApplicantId,
        jobId = jobId,
        userId = userId,
        username = username,
        headline = headline,
        status = status ?: "PENDING",
        appliedAt = appliedAt
    )

    private fun ApplicantEntity.toDto() = ApplicantDto(
        id = if (id != 0L) id else applicantId,
        applicationId = applicationId,
        backendApplicantId = backendApplicantId,
        userId = userId,
        username = username,
        headline = headline,
        status = status,
        appliedAt = appliedAt
    )
}
