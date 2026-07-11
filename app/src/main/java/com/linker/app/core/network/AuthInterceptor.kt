package com.linker.app.core.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            // Add auth header here if needed
            // .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}
