package com.linker.app.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.linker.app.core.database.entity.MyJobEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MyJobDao {
    @Upsert
    suspend fun upsertMyJobs(jobs: List<MyJobEntity>)

    @Query("SELECT * FROM my_jobs ORDER BY createdAt DESC")
    fun getMyJobsFlow(): Flow<List<MyJobEntity>>

    @Query("SELECT * FROM my_jobs WHERE id = :jobId")
    suspend fun getMyJobById(jobId: Long): MyJobEntity?

    @Query("DELETE FROM my_jobs WHERE id = :jobId")
    suspend fun deleteMyJobById(jobId: Long)

    @Query("DELETE FROM my_jobs")
    suspend fun deleteAllMyJobs()
}
