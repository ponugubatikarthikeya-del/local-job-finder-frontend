package com.linker.app.core.network_api.Interceptor

import com.linker.app.core.datastore.TokenManager
import com.linker.app.core.network_api.AuthEventBus
import com.linker.app.core.network_api.api.TokenRefreshService
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.withLock

/**
 * Native OkHttp Authenticator for thread-safe 401 response handling and token refreshing.
 * Employs synchronized double-checked locking to ensure only one concurrent network request triggers token refresh,
 * while preventing infinite retry loops.
 */
@Singleton
class AuthAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val tokenRefreshService: TokenRefreshService,
    private val authEventBus: AuthEventBus
) : Authenticator {

    private val refreshLock = ReentrantLock()

    override fun authenticate(route: Route?, response: Response): Request? {
        val path = response.request.url.encodedPath

        // Do not attempt refresh on auth endpoints (login, signup, refresh, etc.)
        if (path.contains("auth/refresh") || path.contains("user/auth/login") || path.contains("user/auth/signup")) {
            return null
        }

        // Limit retry count to avoid infinite loops on persistent 401s
        if (responseCount(response) >= 2) {
            return null
        }

        val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")?.trim()

        return refreshLock.withLock {
            val updatedToken = tokenManager.getAccessTokenSync()

            // If another concurrent thread refreshed the token while we waited on the lock, replay with updated token
            if (!updatedToken.isNullOrBlank() && updatedToken != requestToken) {
                return@withLock response.request.newBuilder()
                    .header("Authorization", "Bearer $updatedToken")
                    .build()
            }

            val refreshToken = tokenManager.getRefreshTokenSync()
            if (refreshToken.isNullOrBlank()) {
                tokenManager.clearTokensSync()
                authEventBus.emitLogout()
                return@withLock null
            }

            try {
                // Synchronous execute() on OkHttp's worker thread - NO runBlocking!
                val refreshCall = tokenRefreshService.refreshTokens("Bearer $refreshToken").execute()

                if (refreshCall.isSuccessful) {
                    val dataMap = refreshCall.body()?.data
                    val newAccessToken = dataMap?.get("accessToken")
                    val newRefreshToken = dataMap?.get("refreshToken")

                    if (!newAccessToken.isNullOrBlank() && !newRefreshToken.isNullOrBlank()) {
                        tokenManager.saveTokensSync(newAccessToken, newRefreshToken)

                        return@withLock response.request.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()
                    }
                }

                // Token refresh failed or response invalid -> wipe tokens and trigger logout
                tokenManager.clearTokensSync()
                authEventBus.emitLogout()
                null
            } catch (e: Exception) {
                tokenManager.clearTokensSync()
                authEventBus.emitLogout()
                null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }
        return count
    }
}
