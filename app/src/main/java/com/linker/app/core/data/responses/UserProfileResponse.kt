package com.linker.app.core.data.responses

import com.linker.app.core.data.models.UserDto
import java.time.Instant

data class UserProfileResponse<T>(
    val timestamp: Instant,
    val status: Int = 0,
    val errorCode: String? = null,
    val message: String? = null,
    val path: String? = null,
    val data : T?
)
