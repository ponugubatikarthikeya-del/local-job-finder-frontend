package com.linker.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.linker.app.core.database.entity.ApplicantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertApplicants(applicants: List<ApplicantEntity>)

    @Query("SELECT * FROM applicants WHERE jobId = :jobId")
    fun getApplicantsForJob(jobId: Long): Flow<List<ApplicantEntity>>

    @Query("UPDATE applicants SET status = :status WHERE applicantId = :applicantId")
    suspend fun updateApplicantStatus(applicantId: Long, status: String)

    @Query("DELETE FROM applicants WHERE jobId = :jobId")
    suspend fun deleteApplicantsForJob(jobId: Long)
}
