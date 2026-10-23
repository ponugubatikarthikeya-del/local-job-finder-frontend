package com.linker.app.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.linker.app.core.util.enums.JobStatusEnum
import com.linker.app.core.util.enums.JobTypeEnum
import java.math.BigDecimal
import java.time.Instant

@Entity(
    tableName = "my_jobs",
    indices = [
        Index(value = ["createdAt"])
    ]
)
data class MyJobEntity(
    @PrimaryKey
    val id: Long,
    val userId: Long,
    val username: String,
    val title: String,
    val description: String,
    val category: String,
    val payAmount: BigDecimal,
    val jobType: JobTypeEnum,
    val address: String,
    val status: JobStatusEnum,
    val distance: Double,
    val jobTime: Instant?,
    val createdAt: Instant?
)

@Entity(
    tableName = "my_jobs_remote_keys",
    indices = [Index(value = ["id"])]
)
data class MyJobRemoteEntity(
    @PrimaryKey
    val id: Long,
    val prevPage: Int?,
    val nextPage: Int?
)
