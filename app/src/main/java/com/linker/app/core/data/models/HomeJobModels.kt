package com.linker.app.core.data.models

import com.linker.app.core.util.enums.JobStatusEnum
import com.linker.app.core.util.enums.JobTypeEnum
import java.math.BigDecimal

data class HomeJobCard(
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
    val jobTime: java.time.LocalDateTime,
    val createdAt: java.time.LocalDateTime
)
