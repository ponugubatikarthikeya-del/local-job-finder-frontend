package com.linker.app.core.network_api.repository

import com.linker.app.core.data.models.User
import com.linker.app.core.data.request.AddressTakeRequest
import com.linker.app.core.data.request.EditProfileRequest
import com.linker.app.core.data.responses.ProfileResponse
import com.linker.app.core.network_api.NetworkResult
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun updateAddress(request: AddressTakeRequest): NetworkResult<ProfileResponse>
    suspend fun getMyProfile(): NetworkResult<Unit>
    suspend fun updateProfile(request: EditProfileRequest): NetworkResult<ProfileResponse>
    fun getUserProfileFlow(): Flow<User?>
    suspend fun clearProfile()
}
