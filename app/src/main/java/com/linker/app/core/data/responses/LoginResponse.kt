package com.linker.app.core.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse<T>(
    val timestamp: String, // Instant represented as String for serialization
    val status: Int,
    val errorCode: String?,
    val message: String,
    val path: String,
    val data : T
)
