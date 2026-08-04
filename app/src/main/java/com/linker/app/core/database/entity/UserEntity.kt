package com.linker.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.Instant

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey
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
