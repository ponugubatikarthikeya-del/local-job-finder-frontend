package com.linker.app.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.linker.app.core.database.entity.JobEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {
    @Upsert
    suspend fun upsertJobs(jobs: List<JobEntity>)

    @Query("SELECT * FROM jobs")
    fun getJobsFlow(): Flow<List<JobEntity>>

    @Query("DELETE FROM jobs")
    suspend fun deleteAllJobs()
}