package com.linker.app.core.data.responses

import kotlinx.serialization.Serializable
import java.time.Instant
import com.linker.app.core.util.serializers.InstantSerializer

@Serializable
data class ApplicantDto(
    val applicantId: Long,
    val userId: Long,
    val username: String,
    val headline: String? = null,
    val status: String? = "PENDING",
    @Serializable(with = InstantSerializer::class)
    val appliedAt: Instant? = null
)
