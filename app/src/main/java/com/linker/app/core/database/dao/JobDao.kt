package com.linker.app.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.linker.app.core.database.entity.JobEntity
import com.linker.app.core.database.entity.JobRemoteEntity

@Dao
interface JobDao {
    @Upsert
    suspend fun upsertJobs(jobs: List<JobEntity>)

    @Query("SELECT * FROM jobs ORDER BY createdAt DESC")
    fun getJobsFlow(): PagingSource<Int, JobEntity>

    @Query("SELECT * FROM jobs WHERE id = :jobId")
    suspend fun getJobById(jobId: Long): JobEntity?

    @Query("DELETE FROM jobs WHERE id = :jobId")
    suspend fun deleteJobById(jobId: Long)

    @Query("SELECT COUNT(*) FROM jobs")
    suspend fun getJobCount(): Int

    @Query("DELETE FROM jobs")
    suspend fun deleteAllJobs()
}

@Dao
interface JobRemoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<JobRemoteEntity>)

    @Query("DELETE FROM jobs_remote_keys")
    suspend fun clearRemoteKeys()

    @Query("SELECT * FROM jobs_remote_keys WHERE jobId = :jobId")
    suspend fun remoteKeysJobId(jobId: Long): JobRemoteEntity?
}
