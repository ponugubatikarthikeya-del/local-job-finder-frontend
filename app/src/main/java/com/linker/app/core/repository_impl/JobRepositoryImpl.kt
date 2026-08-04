package com.linker.app.core.repository_impl

import com.linker.app.core.database.dao.JobDao
import com.linker.app.core.database.dao.MyJobDao
import com.linker.app.core.database.entity.JobEntity
import com.linker.app.core.database.entity.MyJobEntity
import com.linker.app.core.network_api.AppException
import com.linker.app.core.network_api.api.JobApiService
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.data.models.HomeJobCard
import com.linker.app.core.data.request.PostJobRequest
import com.linker.app.core.data.responses.JobDto
import com.linker.app.core.data.responses.UserResponse
import com.linker.app.core.network_api.repository.JobRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class JobRepositoryImpl @Inject constructor(
    private val apiService: JobApiService,
    private val jobDao: JobDao,
    private val myJobDao: MyJobDao,
    private val ioDispatcher: CoroutineDispatcher
) : JobRepository {

    override suspend fun getActiveJobsNearby(
        lat: Double,
        lng: Double,
        radius: Double,
        page: Int,
        size: Int
    ): NetworkResult<Unit> = withContext(ioDispatcher) {
        try {
            val response = apiService.getActiveJobs(
                latitude = lat,
                longitude = lng,
                radius = radius,
                page = page,
                size = size
            )
            val entities = response.allJobs.map { it.toJobEntity() }
            jobDao.deleteAllJobs() // Clear old jobs for nearby results
            jobDao.upsertJobs(entities)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("JOB_REPO", "Error fetching nearby jobs: ${e.message}", e)
            NetworkResult.Failure(AppException.NetworkException(e.message ?: "Failed to fetch nearby jobs", e))
        }
    }

    override suspend fun postJob(request: PostJobRequest): NetworkResult<UserResponse> = withContext(ioDispatcher) {
        try {
            val response = apiService.postJob(request)
            NetworkResult.Success(response)
        } catch (e: retrofit2.HttpException) {
            NetworkResult.Failure(AppException.ServerException(e.code(), e.message()))
        } catch (e: java.io.IOException) {
            NetworkResult.Failure(AppException.NetworkException("No internet connection", e))
        } catch (e: Exception) {
            NetworkResult.Failure(AppException.UnknownException(e.localizedMessage ?: "Unknown error"))
        }
    }

    override suspend fun getMyJobs(page: Int, size: Int): NetworkResult<Unit> = withContext(ioDispatcher) {
        try {
            val response = apiService.getMyJobs(page, size)
            val entities = response.allJobs.map { it.toMyJobEntity() }
            if (page == 0) {
                myJobDao.deleteAllMyJobs()
            }
            myJobDao.upsertMyJobs(entities)
            NetworkResult.Success(Unit)
        } catch (e: retrofit2.HttpException) {
            NetworkResult.Failure(AppException.ServerException(e.code(), e.message()))
        } catch (e: java.io.IOException) {
            NetworkResult.Failure(AppException.NetworkException("No internet connection", e))
        } catch (e: Exception) {
            NetworkResult.Failure(AppException.UnknownException(e.localizedMessage ?: "Unknown error"))
        }
    }

    override suspend fun searchJobs(
        lat: Double,
        lng: Double,
        search: String?,
        category: String?,
        radius: Double,
        page: Int,
        size: Int
    ): NetworkResult<Unit> = withContext(ioDispatcher) {
        try {
            val response = apiService.searchJobs(
                latitude = lat,
                longitude = lng,
                radius = radius,
                search = search,
                category = category,
                page = page,
                size = size
            )
            val entities = response.allJobs.map { it.toJobEntity() }
            if (page == 0) {
                jobDao.deleteAllJobs() // Clear results for new search
            }
            jobDao.upsertJobs(entities)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("JOB_REPO", "Error searching jobs: ${e.message}", e)
            NetworkResult.Failure(AppException.NetworkException(e.message ?: "Search failed", e))
        }
    }

    override suspend fun getJobs(): NetworkResult<List<HomeJobCard>> = withContext(ioDispatcher) {
        try {
            // This might be deprecated now in favor of nearby jobs, but keeping for compatibility
            NetworkResult.Failure(AppException.UnknownException("Use getActiveJobsNearby"))
        } catch (e: Exception) {
            NetworkResult.Failure(AppException.NetworkException(e.message ?: "Unknown error", e))
        }
    }

    override suspend fun syncJobs(): NetworkResult<Unit> = withContext(ioDispatcher) {
        // Dummy implementation since we need lat/lng now
        NetworkResult.Success(Unit)
    }

    override fun getJobsFlow(): Flow<List<HomeJobCard>> {
        return jobDao.getJobsFlow().map { entities ->
            entities.map { it.toHomeJobCard() }
        }.flowOn(ioDispatcher)
    }

    override fun getMyJobsFlow(): Flow<List<HomeJobCard>> {
        return myJobDao.getMyJobsFlow().map { entities ->
            entities.map { it.toHomeJobCard() }
        }.flowOn(ioDispatcher)
    }

    override suspend fun getJobById(jobId: Long): JobEntity? = withContext(ioDispatcher) {
        jobDao.getJobById(jobId) ?: myJobDao.getMyJobById(jobId)?.toJobEntity()
    }

    override suspend fun clearJobs() = withContext(ioDispatcher) {
        jobDao.deleteAllJobs()
        myJobDao.deleteAllMyJobs()
    }

    private fun JobEntity.toHomeJobCard() = HomeJobCard(
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

    private fun MyJobEntity.toHomeJobCard() = HomeJobCard(
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

    private fun MyJobEntity.toJobEntity() = JobEntity(
        id = id,
        userId = userId,
        username = username,
        title = title,
        description = description,
        category = category,
        payAmount = payAmount,
        jobType = jobType,
        address = address,
        status = status,
        distance = distance,
        jobTime = jobTime,
        createdAt = createdAt
    )

    private fun JobDto.toJobEntity() = JobEntity(
        id = id,
        userId = userId,
        username = username ?: "Unknown",
        title = title,
        description = description,
        category = category,
        payAmount = payAmount,
        jobType = jobType,
        address = address,
        status = status,
        distance = distance,
        jobTime = jobTime,
        createdAt = createdAt
    )

    private fun JobDto.toMyJobEntity() = MyJobEntity(
        id = id,
        userId = userId,
        username = username ?: "Unknown",
        title = title,
        description = description,
        category = category,
        payAmount = payAmount,
        jobType = jobType,
        address = address,
        status = status,
        distance = distance,
        jobTime = jobTime,
        createdAt = createdAt
    )
}
