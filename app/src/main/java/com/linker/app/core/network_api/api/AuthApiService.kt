package com.linker.app.core.network_api.api


import com.linker.app.core.data.request.EmailCheckupRequest
import com.linker.app.core.data.request.PasswordRequest
import com.linker.app.core.data.request.LoginRequest
import com.linker.app.core.data.request.ResetPasswordOtpValidateRequest
import com.linker.app.core.data.request.SignupOtpValidationRequest
import com.linker.app.core.data.request.SignupRequest
import com.linker.app.core.data.responses.AuthResponse
import com.linker.app.core.data.responses.LoginResponse
import com.linker.app.core.data.responses.RefreshTokenResponse
import com.linker.app.core.data.responses.ResetKeyOtpResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthApiService {

    @POST("user/auth/signup")
    suspend fun signup(@Body request: SignupRequest): AuthResponse
    @POST("user/auth/signupOtp-verification")
    suspend fun verifyOtp(@Body request: SignupOtpValidationRequest): LoginResponse

    @POST("user/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    @POST("user/auth/refresh/token")
    suspend fun refreshTokens(@Header("Authorization") refreshToken: String): RefreshTokenResponse

    @PATCH("user/auth/forgot-password")
    suspend fun forgotPassword(@Body request: EmailCheckupRequest): AuthResponse

    @PATCH("user/auth/reset-otp-verify")
    suspend fun verifyResetOtp(@Body request: ResetPasswordOtpValidateRequest): ResetKeyOtpResponse

    @PATCH("user/auth/change-password")
    suspend fun changePassword(@Body request: PasswordRequest): AuthResponse

    @POST("user/auth/resend-otp")
    suspend fun resendOtp(@Body request: EmailCheckupRequest): AuthResponse

    @DELETE("user/auth/logout")
    suspend fun logout(
        @Header("X-Device-Id") deviceId: String = "DEFAULT_DEVICE",
        @Header("Authorization") token: String = "Bearer AUTO",
        @retrofit2.http.Query("allDevices") allDevices: Boolean = false
    ): AuthResponse
}
