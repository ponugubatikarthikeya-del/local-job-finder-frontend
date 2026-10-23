package com.linker.app.core.data.request

import kotlinx.serialization.Serializable

@Serializable
data class SubmitReviewRequest(
    val jobId: Long? = null,
    val rating: Int? = null,
    val comment: String? = null
)
