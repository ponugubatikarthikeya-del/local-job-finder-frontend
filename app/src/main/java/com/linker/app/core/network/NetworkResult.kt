package com.linker.app.core.network


sealed interface NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>
    data class Failure(val exception: AppException) : NetworkResult<Nothing>
}

sealed class AppException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NetworkException(message: String, cause: Throwable? = null) : AppException(message, cause)
    class ServerException(val code: Int, message: String) : AppException(message)
    class UnknownException(message: String, cause: Throwable? = null) : AppException(message, cause)
}