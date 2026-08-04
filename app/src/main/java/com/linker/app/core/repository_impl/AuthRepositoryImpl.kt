package com.linker.app.core.repository_impl

import com.linker.app.core.data.request.EmailCheckupRequest
import com.linker.app.core.data.request.PasswordRequest
import com.linker.app.core.data.request.LoginRequest
import com.linker.app.core.data.request.ResetPasswordOtpValidateRequest
import com.linker.app.core.data.request.SignupOtpValidationRequest
import com.linker.app.core.network_api.api.AuthApiService
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.network_api.safeApiCall
import com.linker.app.core.data.request.SignupRequest
import com.linker.app.core.data.responses.AuthResponse
import com.linker.app.core.data.responses.LoginResponse
import com.linker.app.core.data.responses.RefreshTokenResponse
import com.linker.app.core.data.responses.ResetKeyOtpResponse
import com.linker.app.core.datastore.TokenManager

import com.linker.app.core.network_api.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val tokenManager: TokenManager,
    private val ioDispatcher: CoroutineDispatcher
) : AuthRepository {

    override suspend fun signup(request: SignupRequest): NetworkResult<AuthResponse> {
        return safeApiCall(ioDispatcher) {
            apiService.signup(request)
        }
    }

    override suspend fun verifyOtp(request: SignupOtpValidationRequest): NetworkResult<LoginResponse> {
        return safeApiCall(ioDispatcher) {
            apiService.verifyOtp(request)
        }
    }

    override suspend fun login(request: LoginRequest): NetworkResult<LoginResponse> {
        return safeApiCall(ioDispatcher) {
            apiService.login(request)
        }
    }

    override suspend fun refreshTokens(): NetworkResult<RefreshTokenResponse> {
        return safeApiCall(ioDispatcher) {
            val refreshToken = tokenManager.refreshToken.firstOrNull()
                ?: throw Exception("No refresh token found")
            apiService.refreshTokens("Bearer $refreshToken")
        }
    }

    override suspend fun forgotPassword(request: EmailCheckupRequest): NetworkResult<AuthResponse> {
        return safeApiCall(ioDispatcher) {
            apiService.forgotPassword(request)
        }
    }

    override suspend fun verifyResetOtp(request: ResetPasswordOtpValidateRequest): NetworkResult<ResetKeyOtpResponse> {
        return safeApiCall(ioDispatcher) {
            apiService.verifyResetOtp(request)
        }
    }

    override suspend fun changePassword(request: PasswordRequest): NetworkResult<AuthResponse> {
        return safeApiCall(ioDispatcher) {
            apiService.changePassword(request)
        }
    }

    override suspend fun resendOtp(request: EmailCheckupRequest): NetworkResult<AuthResponse> {
        return safeApiCall(ioDispatcher) {
            apiService.resendOtp(request)
        }
    }

    override suspend fun logout(allDevices: Boolean): NetworkResult<AuthResponse> {
        return safeApiCall(ioDispatcher) {
            apiService.logout(deviceId = "DEFAULT_DEVICE", allDevices = allDevices)
        }
    }
}
