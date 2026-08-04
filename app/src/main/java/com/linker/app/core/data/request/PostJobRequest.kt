package com.linker.app.core.data.request

import com.linker.app.core.util.enums.JobTypeEnum
import java.math.BigDecimal
import java.time.Instant

data class PostJobRequest(
    val title: String,
    val description: String,
    val category: String,
    val payAmount: BigDecimal,
    val jobType: JobTypeEnum,
    val latitude: BigDecimal,
    val longitude: BigDecimal,
    val address: String,
    val jobTime: Instant
)
