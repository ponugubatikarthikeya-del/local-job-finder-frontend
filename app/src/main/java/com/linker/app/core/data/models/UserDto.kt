package com.linker.app.core.data.models

import java.math.BigDecimal
import java.time.Instant

data class UserDto(
    val id: Long,
    val username: String,
    val email: String,
    val phone: String? = null,
    val headline: String? = null,
    val skills: List<String> = emptyList(),
    val dateOfBirth: String? = null,
    val cityName: String? = null,
    val rating: BigDecimal = BigDecimal.ZERO,
    val totalJobsCompleted: Int = 0,
    val userDescription: String? = null,
    val createdAt: Instant = Instant.now()
)
