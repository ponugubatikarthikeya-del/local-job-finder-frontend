package com.linker.app.core.network_api.di

import com.linker.app.core.network_api.api.ApplicationsApiService
import com.linker.app.core.network_api.api.AuthApiService
import com.linker.app.core.network_api.api.FcmApiService
import com.linker.app.core.network_api.api.JobApiService
import com.linker.app.core.network_api.api.JobFilterApiService
import com.linker.app.core.network_api.api.ProfileApiService
import com.linker.app.core.network_api.api.ReviewApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {


    @Provides
    @Singleton
    fun provideApplicationsApiService(retrofit: Retrofit): ApplicationsApiService {
        return retrofit.create(ApplicationsApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFcmApiService(retrofit: Retrofit): FcmApiService {
        return retrofit.create(FcmApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideJobApiService(retrofit: Retrofit): JobApiService {
        return retrofit.create(JobApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideJobFilterApiService(retrofit: Retrofit): JobFilterApiService {
        return retrofit.create(JobFilterApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileApiService(retrofit: Retrofit): ProfileApiService {
        return retrofit.create(ProfileApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideReviewApiService(retrofit: Retrofit): ReviewApiService {
        return retrofit.create(ReviewApiService::class.java)
    }


}
