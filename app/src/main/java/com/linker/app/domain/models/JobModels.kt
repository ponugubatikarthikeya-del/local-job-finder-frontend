package com.linker.app.domain.models

import java.math.BigDecimal
import java.time.LocalDateTime

enum class JobTypeEnum {
    FULL_TIME, PART_TIME, CONTRACT, FREELANCE
}

enum class JobStatusEnum {
    OPEN, CLOSED, IN_PROGRESS, COMPLETED
}

data class CompleteJob(
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
    val jobTime: LocalDateTime,
    val createdAt: LocalDateTime
)

data class JobCard(
    val id: Long,
    val userId: Long,
    val username: String,
    val title: String,
    val category: String,
    val payAmount: BigDecimal,
    val jobType: JobTypeEnum,
    val address: String,
    val status: JobStatusEnum,
    val distance: Double
)
