package com.linker.app.core.data.responses

data class PagedResponse<T>(
    val content: List<T>,
    val last: Boolean,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int
)
