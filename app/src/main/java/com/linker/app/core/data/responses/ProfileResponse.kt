package com.linker.app.core.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val timestamp: String,
    val status: Int = 0,
    val errorCode: String? = null,
    val message: String? = null,
    val path: String? = null
)