package com.linker.app.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.linker.app.core.network_api.repository.FcmRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmTokenManager @Inject constructor(
    private val fcmRepository: FcmRepository
) {
    companion object {
        private const val TAG = "FcmTokenManager"
    }

    /**
     * Fetches current token from Firebase and registers it with the backend.
     * Use this after login or in MainActivity on startup.
     */
    suspend fun syncCurrentToken() = withContext(Dispatchers.IO) {
        try {
            val token = FirebaseMessaging.getInstance().token.await()
            Log.d(TAG, "Syncing current FCM Token: $token")
            fcmRepository.registerToken(token)
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing FCM token", e)
        }
    }
}
