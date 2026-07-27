package com.linker.app.core.repository_impl

import com.linker.app.core.database.dao.JobDao
import com.linker.app.core.database.entity.JobEntity
import com.linker.app.core.network_api.AppException
import com.linker.app.core.network_api.api.JobApiService
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.data.models.JobCard
import com.linker.app.core.network_api.repository.JobRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class JobRepositoryImpl @Inject constructor(
    private val apiService: JobApiService,
    private val jobDao: JobDao,
    private val ioDispatcher: CoroutineDispatcher
) : JobRepository {

    override suspend fun getJobs(): NetworkResult<List<JobCard>> = withContext(ioDispatcher) {
        try {
            val response = apiService.getJobs()
            NetworkResult.Success(response)
        } catch (e: Exception) {
            NetworkResult.Failure(AppException.NetworkException(e.message ?: "Unknown error", e))
        }
    }

    override suspend fun syncJobs(): NetworkResult<Unit> = withContext(ioDispatcher) {
        try {
            val response = apiService.getJobs()
            jobDao.upsertJobs(response.map { it.toJobEntity() })
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Failure(AppException.NetworkException(e.message ?: "Sync failed", e))
        }
    }

    override fun getJobsFlow(): Flow<List<JobCard>> {
        return jobDao.getJobsFlow().map { entities ->
            entities.map { it.toJobCard() }
        }
    }

    override suspend fun clearJobs() = withContext(ioDispatcher) {
        jobDao.deleteAllJobs()
    }

    private fun JobEntity.toJobCard() = JobCard(
        id = id,
        userId = userId,
        username = username,
        title = title,
        category = category,
        payAmount = payAmount,
        jobType = jobType,
        address = address,
        status = status,
        distance = distance
    )

    private fun JobCard.toJobEntity() = JobEntity(
        id = id,
        userId = userId,
        username = username,
        title = title,
        category = category,
        payAmount = payAmount,
        jobType = jobType,
        address = address,
        status = status,
        distance = distance
    )
}
