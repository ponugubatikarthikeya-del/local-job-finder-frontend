package com.linker.app.core.network_api.Interceptor

import com.linker.app.core.datastore.TokenManager
import com.linker.app.core.network_api.api.TokenRefreshService
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenManager,
    private val tokenRefreshService: TokenRefreshService
) : Interceptor {

    // ReentrantLock provides better performance and control than synchronized(this)
    private val refreshLock = ReentrantLock()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // 1. Fetch persistent Device ID and Access Token
        // NOTE: If getOrCreateDeviceId() or accessToken performs Disk I/O,
        // ensure your DataStore uses Dispatchers.IO internally.
        val (deviceId, accessToken) = runBlocking {
            val deviceId = tokenStorage.getOrCreateDeviceId()
            val token = tokenStorage.accessToken.firstOrNull()
            Pair(deviceId, token)
        }

        // 2. Smart Header Replacement
        val currentDeviceIdHeader = originalRequest.header("X-Device-Id")
        if (currentDeviceIdHeader == null || currentDeviceIdHeader == "DEFAULT_DEVICE") {
            requestBuilder.header("X-Device-Id", deviceId)
        }

        val authHeader = originalRequest.header("Authorization")
        val isAuthEndpoint = originalRequest.url.encodedPath.contains("auth")

        if (authHeader == "Bearer AUTO") {
            if (!accessToken.isNullOrBlank()) {
                requestBuilder.header("Authorization", "Bearer $accessToken")
            } else {
                requestBuilder.removeHeader("Authorization")
            }
        } else if (authHeader == null && !isAuthEndpoint && !accessToken.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $accessToken")
        }

        val requestWithHeaders = requestBuilder.build()
        val response = chain.proceed(requestWithHeaders)

        // 3. Handle 401 Unauthorized for non-auth requests (Token Expiration)
        if (response.code == 401 && !isAuthEndpoint) {

            // Check if another thread already refreshed the token while this request was in-flight
            val latestAccessToken = runBlocking {
                tokenStorage.accessToken.firstOrNull()
            }

            if (latestAccessToken != accessToken && !latestAccessToken.isNullOrBlank()) {
                response.close() // CRITICAL: Always close the old response before retrying
                return chain.proceed(
                    originalRequest.newBuilder()
                        .header("X-Device-Id", deviceId)
                        .header("Authorization", "Bearer $latestAccessToken")
                        .build()
                )
            }

            // Synchronize token refresh execution using an explicit Lock
            refreshLock.withLock {
                // Re-verify token inside the lock to handle race conditions
                val tokenAfterLock = runBlocking {
                    tokenStorage.accessToken.firstOrNull()
                }

                if (tokenAfterLock != accessToken && !tokenAfterLock.isNullOrBlank()) {
                    response.close()
                    return chain.proceed(
                        originalRequest.newBuilder()
                            .header("X-Device-Id", deviceId)
                            .header("Authorization", "Bearer $tokenAfterLock")
                            .build()
                    )
                }

                val refreshToken = runBlocking {
                    tokenStorage.refreshToken.firstOrNull()
                }

                if (refreshToken == null) {
                    forceLogout()
                    return response
                }

                try {
                    // Synchronous execution is fine here because OkHttp runs interceptors on background worker threads
                    val refreshCall = tokenRefreshService.refreshTokens("Bearer $refreshToken")
                    val refreshResponse = refreshCall.execute()

                    if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                        val newTokens = refreshResponse.body()!!

                        runBlocking {
                            tokenStorage.saveTokens(newTokens.accessToken, newTokens.refreshToken)
                        }

                        response.close()
                        val retryRequest = originalRequest.newBuilder()
                            .header("X-Device-Id", deviceId)
                            .header("Authorization", "Bearer ${newTokens.accessToken}")
                            .build()

                        return chain.proceed(retryRequest)
                    } else {
                        forceLogout()
                        return response
                    }
                } catch (e: IOException) {
                    return response
                }
            }
        }

        return response
    }

    private fun forceLogout() {
        runBlocking {
            tokenStorage.clearTokens()
        }
    }
}
