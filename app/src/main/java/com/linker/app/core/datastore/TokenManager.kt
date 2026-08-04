package com.linker.app.core.datastore

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import java.util.UUID

// Extension to create a single DataStore instance across the app
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")

        private val RESET_KEY = stringPreferencesKey("reset_key")
        private val DEVICE_ID_KEY = stringPreferencesKey("device_id")
        private val LATITUDE_KEY = stringPreferencesKey("latitude")
        private val LONGITUDE_KEY = stringPreferencesKey("longitude")
        private val ADDRESS_KEY = stringPreferencesKey("address")
        private val HAS_ENTERED_ADDRESS_KEY = booleanPreferencesKey("has_entered_address")
    }

    val latitude: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LATITUDE_KEY]
    }.distinctUntilChanged()

    val longitude: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LONGITUDE_KEY]
    }.distinctUntilChanged()

    val address: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[ADDRESS_KEY]
    }.distinctUntilChanged()

    val hasEnteredAddress: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[HAS_ENTERED_ADDRESS_KEY] ?: false
    }

    suspend fun saveLocation(lat: Double, lng: Double, address: String) {
        context.dataStore.edit { preferences ->
            preferences[LATITUDE_KEY] = lat.toString()
            preferences[LONGITUDE_KEY] = lng.toString()
            preferences[ADDRESS_KEY] = address
            preferences[HAS_ENTERED_ADDRESS_KEY] = true
        }
    }

    /**
     * Clears user location settings (Useful when resetting address or on full data wipe)
     */
    suspend fun clearLocation() {
        context.dataStore.edit { preferences ->
            preferences.remove(LATITUDE_KEY)
            preferences.remove(LONGITUDE_KEY)
            preferences.remove(ADDRESS_KEY)
            preferences.remove(HAS_ENTERED_ADDRESS_KEY)
        }
    }

    // Expose the Access Token as a Flow
    val accessToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }

    // Expose the Refresh Token as a Flow
    val refreshToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]
    }

    val resetKey : Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[RESET_KEY]
    }

    val deviceIdFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[DEVICE_ID_KEY]
    }

    /**
     * Synchronously gets or generates a unique persistent Device ID.
     * Gets or generates a persistent installation-unique Device UUID.
     * Fully compliant with Google Play target SDK privacy guidelines.
     */
    suspend fun getOrCreateDeviceId(): String {

        val existingDeviceId = deviceIdFlow.firstOrNull()

        if (!existingDeviceId.isNullOrBlank()) {
            return existingDeviceId
        }

        // Generate a cryptographically strong UUID
        val newDeviceId = UUID.randomUUID().toString()

        context.dataStore.edit { preferences ->
            preferences[DEVICE_ID_KEY] = newDeviceId
        }

        return newDeviceId
    }

    /**
     * Decodes JWT payload locally to check "exp" claim.
     * No network calls involved.
     */
    fun isTokenExpiredLocally(token: String?): Boolean {
        if (token.isNullOrBlank()) return true
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return true
            
            val payload = String(Base64.decode(parts[1], Base64.DEFAULT))
            val json = JSONObject(payload)
            val exp = json.optLong("exp", 0)
            val currentTimeSeconds = System.currentTimeMillis() / 1000
            
            // Adding 10s buffer for clock skew
            currentTimeSeconds >= (exp - 10)
        } catch (e: Exception) {
            true // If decoding fails, treat as expired
        }
    }

    // Save tokens securely
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    // Clear tokens on logout
    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(RESET_KEY)
        }
    }

    /**
     * Fully resets the user session by clearing tokens and location data.
     */
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(RESET_KEY)
            preferences.remove(LATITUDE_KEY)
            preferences.remove(LONGITUDE_KEY)
            preferences.remove(ADDRESS_KEY)
            preferences.remove(HAS_ENTERED_ADDRESS_KEY)
        }
    }

    suspend fun saveResetKey(resetKey: String) {
        context.dataStore.edit { preferences ->
            preferences[RESET_KEY] = resetKey
        }
    }

    suspend fun clearResetKey(){
        context.dataStore.edit { preferences ->
            preferences.remove(RESET_KEY)
        }
    }

}
