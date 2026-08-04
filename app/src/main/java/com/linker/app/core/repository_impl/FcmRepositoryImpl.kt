package com.linker.app.core.repository_impl

import com.linker.app.core.data.request.FcmTokenRequest
import com.linker.app.core.data.responses.AuthResponse
import com.linker.app.core.network_api.AppException
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.network_api.api.FcmApiService
import com.linker.app.core.network_api.repository.FcmRepository
import com.linker.app.core.network_api.safeApiCall
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class FcmRepositoryImpl @Inject constructor(
    private val apiService: FcmApiService,
    private val ioDispatcher: CoroutineDispatcher
) : FcmRepository {

    override suspend fun registerToken(token: String): NetworkResult<AuthResponse> {
        return safeApiCall(ioDispatcher) {
            apiService.registerFcmToken(FcmTokenRequest(token))
        }
    }
}
