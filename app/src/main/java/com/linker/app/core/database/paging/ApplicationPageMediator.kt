package com.linker.app.core.database.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.linker.app.core.database.app_database.JobDatabase
import com.linker.app.core.database.entity.ApplicantEntity
import com.linker.app.core.database.entity.ApplicantRemoteEntity
import com.linker.app.core.network_api.api.ApplicationsApiService
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Paging 3 RemoteMediator for fetching applicant records scoped to a specific job ID.
 * Preserves all application/applicant ID fields cleanly for backend calls.
 */
@OptIn(ExperimentalPagingApi::class)
class ApplicationPageMediator @Inject constructor(
    private val jobId: Long,
    private val apiService: ApplicationsApiService,
    private val database: JobDatabase
) : RemoteMediator<Int, ApplicantEntity>() {

    override suspend fun initialize(): InitializeAction {
        val applicantCount = database.applicantDao().getApplicantCount(jobId)
        return if (applicantCount > 0) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ApplicantEntity>
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
            val response = apiService.viewApplicants(jobId = jobId, page = page, size = size)

            val body = response.data
            val applicants = body.content
            val totalPages = body.totalPages
            val endOfPaginationReached = body.last || applicants.isEmpty() || page >= totalPages - 1

            if (applicants.isEmpty()) {
                if (loadType == LoadType.REFRESH) {
                    database.withTransaction {
                        database.applicantRemoteDao().clearRemoteKeysForJob(jobId)
                        database.applicantDao().deleteApplicantsForJob(jobId)
                    }
                }
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.applicantRemoteDao().clearRemoteKeysForJob(jobId)
                    database.applicantDao().deleteApplicantsForJob(jobId)
                }

                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = applicants.map {
                    ApplicantRemoteEntity(
                        applicantId = it.uniqueRoomKey,
                        jobId = jobId,
                        prevPage = prevKey,
                        nextPage = nextKey
                    )
                }

                database.applicantRemoteDao().insertAll(keys)
                database.applicantDao().upsertApplicants(applicants.map {
                    ApplicantEntity(
                        applicantId = it.uniqueRoomKey,
                        applicationId = it.applicationId,
                        id = it.id,
                        backendApplicantId = it.backendApplicantId,
                        jobId = jobId,
                        userId = it.userId,
                        username = it.username,
                        headline = it.headline,
                        status = it.status ?: "PENDING",
                        appliedAt = it.appliedAt
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

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ApplicantEntity>): ApplicantRemoteEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { applicant ->
                database.applicantRemoteDao().remoteKeysApplicantId(applicant.applicantId)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ApplicantEntity>): ApplicantRemoteEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { applicant ->
                database.applicantRemoteDao().remoteKeysApplicantId(applicant.applicantId)
            }
    }
}
