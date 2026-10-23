package com.linker.app.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.linker.app.core.database.entity.MyJobEntity
import com.linker.app.core.database.entity.MyJobRemoteEntity

@Dao
interface MyJobDao {
    @Upsert
    suspend fun upsertMyJobs(jobs: List<MyJobEntity>)

    @Query("SELECT * FROM my_jobs ORDER BY createdAt DESC")
    fun getMyJobsFlow(): PagingSource<Int, MyJobEntity>

    @Query("SELECT * FROM my_jobs WHERE id = :jobId")
    suspend fun getMyJobById(jobId: Long): MyJobEntity?

    @Query("DELETE FROM my_jobs WHERE id = :jobId")
    suspend fun deleteMyJobById(jobId: Long)

    @Query("SELECT COUNT(*) FROM my_jobs")
    suspend fun getMyJobCount(): Int

    @Query("DELETE FROM my_jobs")
    suspend fun deleteAllMyJobs()
}

@Dao
interface MyJobsRemoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<MyJobRemoteEntity>)

    @Query("DELETE FROM my_jobs_remote_keys")
    suspend fun clearRemoteKeys()

    @Query("SELECT * FROM my_jobs_remote_keys WHERE id = :jobId")
    suspend fun remoteKeysJobId(jobId: Long): MyJobRemoteEntity?
}
