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

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenManager,
    private val tokenRefreshService: TokenRefreshService
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 1. Fetch persistent Device ID and Access Token
        val (deviceId, accessToken) = runBlocking {
            val deviceId = tokenStorage.getOrCreateDeviceId()
            val token = tokenStorage.accessToken.firstOrNull()
            Pair(deviceId, token)
        }

        // 2. Build base request with X-Device-Id header for ALL requests
        val requestBuilder = originalRequest.newBuilder()
            .header("X-Device-Id", deviceId)

//        val requestWithToken = if (accessToken != null) {
//            originalRequest.newBuilder()
//                .header("Authorization", "Bearer $accessToken")
//                .build()
//        } else {
//            originalRequest
//        }

        // 3. Attach Bearer Token ONLY if it's NOT an authentication/signup route
        val isAuthEndpoint = originalRequest.url.encodedPath.contains("auth")
        if (!isAuthEndpoint && !accessToken.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $accessToken")
        }

        val requestWithHeaders = requestBuilder.build()
        val response = chain.proceed(requestWithHeaders)

        // 4. Handle 401 Unauthorized for non-auth requests (Token Expiration)
        if (response.code == 401 && !isAuthEndpoint) {
            synchronized(this) {
                val latestAccessToken = runBlocking {
                    tokenStorage.accessToken.firstOrNull()
                }

                if (latestAccessToken != accessToken && !latestAccessToken.isNullOrBlank()) {
                    response.close()
                    return chain.proceed(
                        originalRequest.newBuilder()
                            .header("X-Device-Id", deviceId)
                            .header("Authorization", "Bearer $latestAccessToken")
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
        // TODO: Broadcast an event or use a shared state to navigate UI to Login
    }
}
