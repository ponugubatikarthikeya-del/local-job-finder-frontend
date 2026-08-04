package com.linker.app.core.network_api.repository

import com.linker.app.core.data.responses.AuthResponse
import com.linker.app.core.network_api.NetworkResult

interface FcmRepository {
    suspend fun registerToken(token: String): NetworkResult<AuthResponse>
}
