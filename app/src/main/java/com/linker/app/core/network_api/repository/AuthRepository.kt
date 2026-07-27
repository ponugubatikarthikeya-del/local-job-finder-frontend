package com.linker.app.core.network_api.repository

import com.linker.app.core.network_api.NetworkResult
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

interface AuthRepository {
    suspend fun signup(request: SignupRequest): NetworkResult<AuthResponse>
    suspend fun verifyOtp(request: SignupOtpValidationRequest): NetworkResult<LoginResponse>
    suspend fun login(request: LoginRequest): NetworkResult<LoginResponse>
    suspend fun refreshTokens(): NetworkResult<RefreshTokenResponse>

    suspend fun forgotPassword(request: EmailCheckupRequest): NetworkResult<AuthResponse>
    suspend fun verifyResetOtp(request: ResetPasswordOtpValidateRequest): NetworkResult<ResetKeyOtpResponse>
    suspend fun changePassword(request: PasswordRequest): NetworkResult<AuthResponse>

    suspend fun resendOtp(request: EmailCheckupRequest): NetworkResult<AuthResponse>
}
