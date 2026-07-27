package com.linker.app.core.data.responses

data class MyProfileResponse(
    val timestamp: String,
    val status: Int = 0,
    val errorCode: String? = null,
    val message: String? = null,
    val path: String? = null,
    val data: Any? = null
)