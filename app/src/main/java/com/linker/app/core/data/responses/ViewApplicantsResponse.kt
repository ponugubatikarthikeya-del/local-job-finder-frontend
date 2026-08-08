package com.linker.app.core.data.responses

import com.linker.app.core.data.models.ApplicantDto
import kotlinx.serialization.Serializable
import java.time.Instant
import com.linker.app.core.util.serializers.InstantSerializer

@Serializable
data class ViewApplicantsResponse(
    @Serializable(with = InstantSerializer::class)
    val timestamp: Instant,
    val status: Int = 0,
    val errorCode: String? = null,
    val message: String? = null,
    val path: String? = null,
    val allJobs: List<ApplicantDto>? = null,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val pageNumber: Int = 0,
    val isLast: Boolean = false
)
