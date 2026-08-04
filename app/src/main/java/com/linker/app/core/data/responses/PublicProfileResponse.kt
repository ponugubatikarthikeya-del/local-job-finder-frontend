package com.linker.app.core.data.responses

import kotlinx.serialization.Serializable
import java.time.Instant
import com.linker.app.core.util.serializers.InstantSerializer

@Serializable
data class PublicProfileResponse(
    @Serializable(with = InstantSerializer::class)
    val timestamp: Instant,
    val status: Int = 0,
    val errorCode: String? = null,
    val message: String? = null,
    val path: String? = null,
    val data: PublicProfileDto? = null
)
