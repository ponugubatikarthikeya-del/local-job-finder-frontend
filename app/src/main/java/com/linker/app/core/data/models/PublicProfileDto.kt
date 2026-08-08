package com.linker.app.core.data.models

import com.linker.app.core.util.serializers.BigDecimalSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class PublicProfileDto(
    val id: Long,
    val username: String,
    val headline: String? = null,
    val skills: List<String>? = null,
    val cityName: String? = null,
    @Serializable(with = BigDecimalSerializer::class)
    val rating: BigDecimal = BigDecimal.ZERO,
    val totalJobsCompleted: Int? = 0,
    val userDescription: String? = null
)