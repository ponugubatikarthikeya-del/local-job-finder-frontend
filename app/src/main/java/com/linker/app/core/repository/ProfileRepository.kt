package com.linker.app.core.repository

import com.linker.app.core.data.models.PublicProfileDto
import com.linker.app.core.data.models.UserDto
import com.linker.app.core.data.request.AddressTakeRequest
import com.linker.app.core.data.request.EditProfileRequest
import com.linker.app.core.data.responses.ProfileResponse
import com.linker.app.core.data.responses.UserProfileResponse
import com.linker.app.core.network_api.NetworkResult
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun updateAddress(request: AddressTakeRequest): NetworkResult<ProfileResponse>
    suspend fun getMyProfile(): NetworkResult<Unit>
    suspend fun getPublicProfile(jobId: Long): NetworkResult<UserProfileResponse<PublicProfileDto>>
    suspend fun updateProfile(request: EditProfileRequest): NetworkResult<ProfileResponse>
    fun getUserProfileFlow(): Flow<UserDto?>
    suspend fun clearProfile()
}
