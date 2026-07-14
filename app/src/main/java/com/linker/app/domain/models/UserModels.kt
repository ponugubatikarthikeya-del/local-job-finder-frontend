package com.linker.app.domain.models

import java.math.BigDecimal
import java.time.Instant

data class User(
    val id: Long,
    val username: String,
    val email: String,
    val phone: String,
    val headline: String,
    val skills: String,
    val dateOfBirth: String,
    val cityName: String,
    val rating: BigDecimal,
    val totalJobsCompleted: Int,
    val userDescription: String,
    val createdAt: Instant
)
