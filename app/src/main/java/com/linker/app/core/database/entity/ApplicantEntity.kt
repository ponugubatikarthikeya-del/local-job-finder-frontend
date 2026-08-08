package com.linker.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "applicants")
data class ApplicantEntity(
    @PrimaryKey
    val applicantId: Long,
    val jobId: Long,
    val userId: Long,
    val username: String,
    val headline: String?,
    val status: String,
    val appliedAt: Instant?
)
