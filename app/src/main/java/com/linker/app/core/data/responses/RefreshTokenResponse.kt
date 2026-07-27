package com.linker.app.core.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponse(
    val timestamp: String, // Instant represented as String for serialization
    val status: Int,
    val errorCode: String?,
    val message: String,
    val path: String,
    val accessToken: String,
    val refreshToken: String
)
