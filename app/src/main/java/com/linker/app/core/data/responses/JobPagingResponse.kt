package com.linker.app.core.data.responses

data class JobPagingResponse<T>(
    val data: T,
    val errorCode: String?,
    val errorMessage: String?,
    val path: String?,
    val status: Int,
    val timestamp: String
)
