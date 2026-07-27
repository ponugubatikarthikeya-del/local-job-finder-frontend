package com.linker.app.core.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class JobDetailsResponse(
    val timestamp: String,
    val status: Int = 0,
    val errorName: String? = null,
    val message: String? = null,
    val path: String? = null,
//    val jobDetails: CompleteJobDto? = null
)