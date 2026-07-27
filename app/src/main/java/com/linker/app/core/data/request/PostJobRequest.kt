package com.linker.app.core.data.request


import com.linker.app.core.util.enums.JobTypeEnum
import java.math.BigDecimal
import java.time.LocalDateTime

data class PostJobRequest(

    val title: String = "",

    val description: String = "",

    val category: String = "",

    val payAmount: BigDecimal? = null,

    val jobType: JobTypeEnum? = null,

    val latitude: BigDecimal? = null,

    val longitude: BigDecimal? = null,

    val address: String = "",

    val jobTime: LocalDateTime? = null
)