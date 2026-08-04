package com.linker.app.core.network_api.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.linker.app.core.network_api.Interceptor.AuthInterceptor
import com.linker.app.core.network_api.api.TokenRefreshService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://172.22.145.99:8080/"

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .registerTypeAdapter(Instant::class.java, JsonDeserializer { json, _, _ ->
            if (json.isJsonNull || json.asString.isNullOrBlank() || json.asString == "null") null else Instant.parse(json.asString)
        })
        .registerTypeAdapter(Instant::class.java, JsonSerializer<Instant> { src, _, _ ->
            JsonPrimitive(src.toString())
        })
        .create()

    @Provides
    @Singleton
    @Named("BasicClient")
    fun provideBasicOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        @Named("BasicClient") basicClient: OkHttpClient
    ): OkHttpClient {
        return basicClient.newBuilder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideTokenRefreshService(@Named("BasicClient") basicClient: OkHttpClient, gson: Gson): TokenRefreshService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(basicClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(TokenRefreshService::class.java)
    }
}
