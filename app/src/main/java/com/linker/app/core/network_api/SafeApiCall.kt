package com.linker.app.core.network_api

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T
): NetworkResult<T> {
    return withContext(dispatcher) {
        try {
            NetworkResult.Success(apiCall())
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> NetworkResult.Failure(
                    AppException.NetworkException("No internet connection or server unreachable", throwable)
                )
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = throwable.response()?.errorBody()?.string() ?: "Unknown Server Error"
                    NetworkResult.Failure(AppException.ServerException(code, errorResponse))
                }
                else -> NetworkResult.Failure(
                    AppException.UnknownException(throwable.localizedMessage ?: "Something went wrong", throwable)
                )
            }
        }
    }
}