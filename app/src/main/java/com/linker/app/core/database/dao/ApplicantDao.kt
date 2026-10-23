package com.linker.app.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.linker.app.core.database.entity.ApplicantEntity
import com.linker.app.core.database.entity.ApplicantRemoteEntity

@Dao
interface ApplicantDao {
    @Upsert
    suspend fun upsertApplicants(applicants: List<ApplicantEntity>)

    @Query("SELECT * FROM applicants WHERE jobId = :jobId ORDER BY appliedAt DESC")
    fun getApplicantsForJob(jobId: Long): PagingSource<Int, ApplicantEntity>

    @Query("UPDATE applicants SET status = :status WHERE jobId = :jobId AND (applicantId = :applicantId OR userId = :applicantId OR backendApplicantId = :applicantId OR id = :applicantId)")
    suspend fun updateApplicantStatus(jobId: Long, applicantId: Long, status: String)

    @Query("SELECT COUNT(*) FROM applicants WHERE jobId = :jobId")
    suspend fun getApplicantCount(jobId: Long): Int

    @Query("DELETE FROM applicants WHERE jobId = :jobId")
    suspend fun deleteApplicantsForJob(jobId: Long)
}

@Dao
interface ApplicantRemoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<ApplicantRemoteEntity>)

    @Query("DELETE FROM applicants_remote_keys WHERE jobId = :jobId")
    suspend fun clearRemoteKeysForJob(jobId: Long)

    @Query("DELETE FROM applicants_remote_keys")
    suspend fun clearRemoteKeys()

    @Query("SELECT * FROM applicants_remote_keys WHERE applicantId = :applicantId")
    suspend fun remoteKeysApplicantId(applicantId: Long): ApplicantRemoteEntity?
}
