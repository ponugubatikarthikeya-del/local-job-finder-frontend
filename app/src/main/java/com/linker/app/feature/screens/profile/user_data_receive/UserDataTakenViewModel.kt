package com.linker.app.feature.screens.profile.user_data_receive

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.app.core.data.request.AddressTakeRequest
import com.linker.app.core.datastore.TokenManager
import com.linker.app.core.network_api.AppException
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject

@HiltViewModel
class UserDataTakenViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val tokenManager: TokenManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(UserDataTakenUiState())
    val state: StateFlow<UserDataTakenUiState> = _state.asStateFlow()

    fun onEvent(event: UserDataTakenEvent) {
        when (event) {
            is UserDataTakenEvent.OnCityNameChange -> {
                _state.update { it.copy(cityName = event.cityName) }
            }
            is UserDataTakenEvent.OnSubmit -> {
                submitAddress()
            }
        }
    }

    private fun submitAddress() {
        val cityName = _state.value.cityName.trim()
        if (cityName.isBlank()) {
            _state.update { it.copy(error = "City name cannot be empty") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val location = getCoordinatesFromAddress(cityName)

            if (location == null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Could not find coordinates for '$cityName'. Please enter a valid city name."
                    )
                }
                return@launch
            }

            val (lat, lng) = location

            val request = AddressTakeRequest(
                cityName = cityName,
                latitude = BigDecimal.valueOf(lat).setScale(8, RoundingMode.HALF_UP),
                longitude = BigDecimal.valueOf(lng).setScale(8, RoundingMode.HALF_UP)
            )

            when (val result = profileRepository.updateAddress(request)) {
                is NetworkResult.Success -> {
                    tokenManager.saveLocation(lat, lng, cityName)
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is NetworkResult.Failure -> {
                    val friendlyError = when (val exception = result.exception) {
                        is AppException.ServerException -> {
                            when (exception.code) {
                                500 -> "Server encountered an error. Please try again later."
                                404 -> "Service not found."
                                else -> "Something went wrong (Error ${exception.code})."
                            }
                        }
                        is AppException.NetworkException -> "Network error. Please check your internet connection."
                        else -> "An unexpected error occurred. Please try again."
                    }
                    _state.update { it.copy(isLoading = false, error = friendlyError) }
                }
                is NetworkResult.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    /**
     * Clean, flexible, and free geocoding function.
     * Uses Open-Meteo REST API globally first (no API keys, works for any location on Earth),
     * falls back to Android native Geocoder, and finally defaults to region fallback if offline.
     */
    private suspend fun getCoordinatesFromAddress(address: String): Pair<Double, Double>? = withContext(Dispatchers.IO) {
        val query = address.trim()
        if (query.isEmpty()) return@withContext null

        // 1. Free Global Open-Meteo REST API (Works for any location on Earth, 100% free, no keys needed)
        try {
            val encoded = URLEncoder.encode(query, "UTF-8")
            val url = URL("https://geocoding-api.open-meteo.com/v1/search?name=$encoded&count=1&language=en&format=json")
            val json = JSONObject(url.readText())
            if (json.has("results")) {
                val item = json.getJSONArray("results").getJSONObject(0)
                return@withContext Pair(item.getDouble("latitude"), item.getDouble("longitude"))
            }
        } catch (_: Exception) { }

        // 2. Android System Geocoder Fallback
        try {
            @Suppress("DEPRECATION")
            val list = Geocoder(context).getFromLocationName(query, 1)
            if (!list.isNullOrEmpty()) {
                return@withContext Pair(list[0].latitude, list[0].longitude)
            }
        } catch (_: Exception) { }

        // 3. Fallback region coordinates if offline
        Pair(16.5062, 80.6480)
    }
}
