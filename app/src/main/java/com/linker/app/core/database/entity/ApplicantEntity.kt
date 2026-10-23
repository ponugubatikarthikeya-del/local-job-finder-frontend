package com.linker.app.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "applicants",
    indices = [
        Index(value = ["jobId"]),
        Index(value = ["applicantId"]),
        Index(value = ["jobId", "applicantId"])
    ]
)
data class ApplicantEntity(
    @PrimaryKey
    val applicantId: Long,
    val applicationId: Long = 0,
    val id: Long = 0,
    val backendApplicantId: Long = 0,
    val jobId: Long,
    val userId: Long,
    val username: String,
    val headline: String?,
    val status: String,
    val appliedAt: Instant?
)

@Entity(
    tableName = "applicants_remote_keys",
    indices = [
        Index(value = ["applicantId"]),
        Index(value = ["jobId"])
    ]
)
data class ApplicantRemoteEntity(
    @PrimaryKey
    val applicantId: Long,
    val jobId: Long,
    val prevPage: Int?,
    val nextPage: Int?
)
