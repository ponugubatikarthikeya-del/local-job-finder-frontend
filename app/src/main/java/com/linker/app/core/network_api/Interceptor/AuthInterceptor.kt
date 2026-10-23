package com.linker.app.core.network_api.Interceptor

import com.linker.app.core.datastore.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Non-blocking OkHttp Interceptor responsible for attaching Device ID and Authorization headers to outgoing requests.
 * Uses synchronous in-memory cached token getters (O(1)) from TokenManager to avoid runBlocking deadlocks.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // Non-blocking, synchronous in-memory lookup - zero runBlocking
        val deviceId = tokenStorage.getDeviceIdSync()
        val accessToken = tokenStorage.getAccessTokenSync()

        // Attach X-Device-Id header if missing or set to DEFAULT_DEVICE
        val currentDeviceIdHeader = originalRequest.header("X-Device-Id")
        if (currentDeviceIdHeader.isNullOrBlank() || currentDeviceIdHeader == "DEFAULT_DEVICE") {
            requestBuilder.header("X-Device-Id", deviceId)
        }

        val authHeader = originalRequest.header("Authorization")
        val isAuthEndpoint = originalRequest.url.encodedPath.contains("auth")

        // Attach Authorization header if missing or specified as "Bearer AUTO"
        if (authHeader == "Bearer AUTO") {
            if (!accessToken.isNullOrBlank()) {
                requestBuilder.header("Authorization", "Bearer $accessToken")
            } else {
                requestBuilder.removeHeader("Authorization")
            }
        } else if (authHeader == null && !isAuthEndpoint && !accessToken.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $accessToken")
        }

        return chain.proceed(requestBuilder.build())
    }
}
