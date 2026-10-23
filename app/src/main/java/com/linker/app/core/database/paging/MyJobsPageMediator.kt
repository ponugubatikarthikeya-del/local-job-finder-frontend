package com.linker.app.core.database.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.linker.app.core.database.app_database.JobDatabase
import com.linker.app.core.database.entity.MyJobEntity
import com.linker.app.core.database.entity.MyJobRemoteEntity
import com.linker.app.core.network_api.api.JobApiService
import retrofit2.HttpException
import java.io.IOException

/**
 * RemoteMediator for user posted jobs ("My Jobs").
 * Triggers LAUNCH_INITIAL_REFRESH when first opened or when app cache is cleared (empty database),
 * while skipping redundant network calls during screen navigation.
 */
@OptIn(ExperimentalPagingApi::class)
class MyJobsPageMediator(
    private val apiService: JobApiService,
    private val database: JobDatabase
) : RemoteMediator<Int, MyJobEntity>() {

    companion object {
        @Volatile
        private var isInitialLoaded = false
    }

    override suspend fun initialize(): InitializeAction {
        val hasCache = database.myJobDao().getMyJobCount() > 0
        return if (!isInitialLoaded || !hasCache) {
            isInitialLoaded = true
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MyJobEntity>
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
            val response = apiService.getMyJobs(page = page, size = size)

            val body = response.data
            val jobs = body.content
            val totalPages = body.totalPages
            val endOfPaginationReached = body.last || jobs.isEmpty() || page >= totalPages - 1

            if (jobs.isEmpty()) {
                if (loadType == LoadType.REFRESH) {
                    database.withTransaction {
                        database.myJobRemoteDao().clearRemoteKeys()
                        database.myJobDao().deleteAllMyJobs()
                    }
                }
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.myJobRemoteDao().clearRemoteKeys()
                    database.myJobDao().deleteAllMyJobs()
                }

                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = jobs.map {
                    MyJobRemoteEntity(
                        id = it.id,
                        prevPage = prevKey,
                        nextPage = nextKey
                    )
                }

                database.myJobRemoteDao().insertAll(keys)
                database.myJobDao().upsertMyJobs(jobs.map {
                    MyJobEntity(
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

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, MyJobEntity>): MyJobRemoteEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { job ->
                database.myJobRemoteDao().remoteKeysJobId(job.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, MyJobEntity>): MyJobRemoteEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { job ->
                database.myJobRemoteDao().remoteKeysJobId(job.id)
            }
    }
}
