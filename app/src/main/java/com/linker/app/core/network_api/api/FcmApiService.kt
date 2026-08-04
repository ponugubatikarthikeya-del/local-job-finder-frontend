package com.linker.app.core.network_api.api

import com.linker.app.core.data.request.FcmTokenRequest
import com.linker.app.core.data.responses.AuthResponse
import retrofit2.http.Body
import retrofit2.http.PUT


interface FcmApiService {

    @PUT("user/fcm/fcm-token/register")
    suspend fun registerFcmToken(
        @Body request: FcmTokenRequest
    ): AuthResponse

}