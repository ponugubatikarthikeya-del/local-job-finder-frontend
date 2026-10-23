package com.linker.app.core.data.responses

data class JobDataResponse<T>(
    val timestamp: String,
    val status: Int,
    val errorCode: String?,
    val errorMessage: String?,
    val path: String?,
    val data: T
)
