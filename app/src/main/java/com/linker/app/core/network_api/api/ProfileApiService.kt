package com.linker.app.core.network_api.api

import com.linker.app.core.data.request.AddressTakeRequest
import com.linker.app.core.data.request.EditProfileRequest
import com.linker.app.core.data.responses.MyProfileResponse
import com.linker.app.core.data.responses.ProfileResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT

interface ProfileApiService {
    @GET("user/profiles/me")
    suspend fun getMyProfile(): MyProfileResponse

    @PATCH("user/profiles/update-address")
    suspend fun updateUserAddress(
        @Body addressTakeRequest: AddressTakeRequest
    ): ProfileResponse

    @PUT("user/profiles/update")
    suspend fun updateProfile(
        @Body editProfileRequest: EditProfileRequest
    ): ProfileResponse
}