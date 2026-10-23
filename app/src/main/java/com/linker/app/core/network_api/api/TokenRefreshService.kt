package com.linker.app.core.network_api.api

import com.linker.app.core.data.responses.LoginResponse
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Dedicated service for token refresh to avoid circular dependencies in Hilt.
 * Uses [Call] instead of suspend to allow synchronous execution in [AuthInterceptor].
 */
interface TokenRefreshService {
    @POST("user/auth/refresh/token")
    fun refreshTokens(
        @Header("Authorization") refreshToken: String,
    ): Call<LoginResponse<Map<String, String>>>
}
