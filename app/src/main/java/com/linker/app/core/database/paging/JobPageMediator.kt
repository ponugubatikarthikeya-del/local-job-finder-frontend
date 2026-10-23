package com.linker.app.core.database.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.linker.app.core.database.app_database.JobDatabase
import com.linker.app.core.database.entity.JobEntity
import com.linker.app.core.database.entity.JobRemoteEntity
import com.linker.app.core.network_api.api.JobApiService
import retrofit2.HttpException
import java.io.IOException

/**
 * RemoteMediator for active jobs.
 * Triggers LAUNCH_INITIAL_REFRESH when app opens, when app cache/data is cleared (empty database),
 * or when new search/category filters are applied, while skipping redundant network refreshes when navigating back.
 */
@OptIn(ExperimentalPagingApi::class)
class JobPageMediator(
    private val search: String?,
    private val category: String?,
    private val lat: Double,
    private val lng: Double,
    private val apiService: JobApiService,
    private val database: JobDatabase
) : RemoteMediator<Int, JobEntity>() {

    companion object {
        @Volatile
        private var isInitialLoaded = false
    }

    override suspend fun initialize(): InitializeAction {
        val isFiltered = !search.isNullOrBlank() || !category.isNullOrBlank()
        val hasCache = database.jobDao().getJobCount() > 0

        // Launch initial refresh if app opens, if app cache is removed/empty, or if search/category filter is applied
        return if (!isInitialLoaded || isFiltered || !hasCache) {
            isInitialLoaded = true
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, JobEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevKey = remoteKeys?.prevPage
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    prevKey
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    nextKey
                }
            }

            val size = state.config.pageSize
            val response = apiService.getActiveJobs(
                search = search,
                category = category,
                latitude = lat,
                longitude = lng,
                page = page,
                size = size
            )

            val body = response.data
            val jobs = body.content
            val totalPages = body.totalPages
            val endOfPaginationReached = body.last || jobs.isEmpty() || page >= totalPages - 1

            if (jobs.isEmpty()) {
                if (loadType == LoadType.REFRESH) {
                    database.withTransaction {
                        database.jobRemoteDao().clearRemoteKeys()
                        database.jobDao().deleteAllJobs()
                    }
                }
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.jobRemoteDao().clearRemoteKeys()
                    database.jobDao().deleteAllJobs()
                }

                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = jobs.map {
                    JobRemoteEntity(
                        jobId = it.id,
                        prevPage = prevKey,
                        nextPage = nextKey
                    )
                }

                database.jobRemoteDao().insertAll(keys)
                database.jobDao().upsertJobs(jobs.map {
                    JobEntity(
                        id = it.id,
                        userId = it.userId,
                        username = it.username ?: "Unknown",
                        title = it.title,
                        description = it.description,
                        category = it.category,
                        payAmount = it.payAmount,
                        jobType = it.jobType,
                        address = it.address ?: "address is not provided",
                        status = it.status,
                        distance = it.distance,
                        jobTime = it.jobTime,
                        createdAt = it.createdAt
                    )
                })
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, JobEntity>): JobRemoteEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { job ->
                database.jobRemoteDao().remoteKeysJobId(job.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, JobEntity>): JobRemoteEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { job ->
                database.jobRemoteDao().remoteKeysJobId(job.id)
            }
    }
}
