package com.linker.app.core.data.responses

import java.time.Instant

data class UserResponse(
    val timestamp: Instant,
    val status: Int = 0,
    val errorCode: String? = null,
    val message: String? = null,
    val path: String? = null,
    val data: Any? = null
)
