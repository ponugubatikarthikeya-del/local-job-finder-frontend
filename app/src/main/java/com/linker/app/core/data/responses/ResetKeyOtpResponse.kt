package com.linker.app.core.data.responses

data class ResetKeyOtpResponse(
    val timestamp: String, // Instant represented as String for serialization
    val status: Int,
    val errorCode: String?,
    val message: String,
    val path: String,
    val resetKey : String
)
