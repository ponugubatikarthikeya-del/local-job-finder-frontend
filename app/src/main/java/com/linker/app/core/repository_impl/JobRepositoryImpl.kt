package com.linker.app.core.repository_impl

import com.linker.app.core.database.entity.JobEntity
import com.linker.app.core.database.entity.MyJobEntity
import com.linker.app.core.network_api.AppException
import com.linker.app.core.network_api.api.ApplicationsApiService
import com.linker.app.core.network_api.api.JobApiService
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.data.models.HomeJobDto
import com.linker.app.core.data.request.PostJobRequest
import com.linker.app.core.data.models.JobDto
import com.linker.app.core.data.responses.JobDataResponse
import com.linker.app.core.data.responses.JobResponse
import com.linker.app.core.repository.JobRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

import com.linker.app.core.database.app_database.JobDatabase
import com.linker.app.core.database.paging.JobPageMediator
import com.linker.app.core.database.paging.MyJobsPageMediator
import androidx.paging.ExperimentalPagingApi

class JobRepositoryImpl @Inject constructor(
    private val apiService: JobApiService,
    private val applicationsApiService: ApplicationsApiService,
    private val database: JobDatabase,
    private val ioDispatcher: CoroutineDispatcher
) : JobRepository {

    private val jobDao = database.jobDao()
    private val myJobDao = database.myJobDao()

    override suspend fun postJob(request: PostJobRequest): NetworkResult<JobDataResponse<JobDto>> = withContext(ioDispatcher) {
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

    override suspend fun applyJob(jobId: Long): NetworkResult<Unit> = withContext(ioDispatcher) {
        try {
            applicationsApiService.applyJob(jobId)
            NetworkResult.Success(Unit)
        } catch (e: HttpException) {
            NetworkResult.Failure(AppException.ServerException(e.code(), e.message()))
        } catch (e: IOException) {
            NetworkResult.Failure(AppException.NetworkException("No internet connection", e))
        } catch (e: Exception) {
            NetworkResult.Failure(AppException.UnknownException(e.localizedMessage ?: "Unknown error"))
        }
    }

    override suspend fun deleteJob(jobId: Long): NetworkResult<JobResponse> = withContext(ioDispatcher) {
        try {
            val response = apiService.deleteJob(jobId)
            // Also delete from local database
            jobDao.deleteJobById(jobId)
            myJobDao.deleteMyJobById(jobId)
            NetworkResult.Success(response)
        } catch (e: retrofit2.HttpException) {
            NetworkResult.Failure(AppException.ServerException(e.code(), e.message()))
        } catch (e: java.io.IOException) {
            NetworkResult.Failure(AppException.NetworkException("No internet connection", e))
        } catch (e: Exception) {
            NetworkResult.Failure(AppException.UnknownException(e.localizedMessage ?: "Unknown error"))
        }
    }

    override suspend fun completeJob(jobId: Long): NetworkResult<JobResponse> = withContext(ioDispatcher) {
        try {
            val response = apiService.completeJob(jobId)
            NetworkResult.Success(response)
        } catch (e: HttpException) {
            NetworkResult.Failure(AppException.ServerException(e.code(), e.message()))
        } catch (e: IOException) {
            NetworkResult.Failure(AppException.NetworkException("No internet connection", e))
        } catch (e: Exception) {
            NetworkResult.Failure(AppException.UnknownException(e.localizedMessage ?: "Unknown error"))
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getJobsFlow(search: String?, category: String?, lat: Double, lng: Double): Flow<PagingData<HomeJobDto>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = JobPageMediator(
                search = search,
                category = category,
                lat = lat,
                lng = lng,
                apiService = apiService,
                database = database
            ),
            pagingSourceFactory = { jobDao.getJobsFlow() }
        ).flow.map { pagingData ->
            pagingData.map { it.toHomeJobDto() }
        }.flowOn(ioDispatcher)
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getMyJobsFlow(): Flow<PagingData<HomeJobDto>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = MyJobsPageMediator(
                apiService = apiService,
                database = database
            ),
            pagingSourceFactory = { myJobDao.getMyJobsFlow() }
        ).flow.map { pagingData ->
            pagingData.map { it.toHomeJobDto() }
        }.flowOn(ioDispatcher)
    }

    override suspend fun getJobById(jobId: Long): JobEntity? = withContext(ioDispatcher) {
        jobDao.getJobById(jobId) ?: myJobDao.getMyJobById(jobId)?.toJobEntity()
    }

    override suspend fun clearJobs() = withContext(ioDispatcher) {
        jobDao.deleteAllJobs()
        myJobDao.deleteAllMyJobs()
    }

    private fun JobEntity.toHomeJobDto() = HomeJobDto(
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

    private fun MyJobEntity.toHomeJobDto() = HomeJobDto(
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
        address = address ?: "No address provided",
        status = status,
        distance = distance ?: 0.0,
        jobTime = jobTime,
        createdAt = createdAt
    )

    private fun JobDto.toMyJobEntity() = MyJobEntity(
        id = id,
        userId = userId,
        username = username ?: "Unknown",
        title = title,
        description = description ,
        category = category ,
        payAmount = payAmount,
        jobType = jobType ,
        address = address ?:"No address provided" ,
        status = status,
        distance = distance,
        jobTime = jobTime,
        createdAt = createdAt
    )
}
