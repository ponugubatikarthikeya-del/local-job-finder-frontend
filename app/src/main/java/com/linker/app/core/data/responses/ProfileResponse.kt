package com.linker.app.core.data.responses

import com.linker.app.core.util.serializers.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class ProfileResponse(
    @Serializable(with = InstantSerializer::class)
    val timestamp: Instant,
    val status: Int = 0,
    val errorCode: String? = null,
    val message: String? = null,
    val path: String? = null
)