package com.linker.app.core.network_api.di

import com.linker.app.core.network_api.api.AuthApiService
import com.linker.app.core.network_api.api.JobApiService
import com.linker.app.core.repository_impl.AuthRepositoryImpl
import com.linker.app.core.repository_impl.JobRepositoryImpl
import com.linker.app.core.network_api.repository.AuthRepository
import com.linker.app.core.network_api.repository.JobRepository
import com.linker.app.core.datastore.TokenManager
import com.linker.app.core.database.dao.JobDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: AuthApiService,
        tokenManager: TokenManager,
        ioDispatcher: CoroutineDispatcher
    ): AuthRepository {
        return AuthRepositoryImpl(apiService, tokenManager, ioDispatcher)
    }

    @Provides
    @Singleton
    fun provideJobRepository(
        apiService: JobApiService,
        jobDao: JobDao,
        ioDispatcher: CoroutineDispatcher
    ): JobRepository {
        return JobRepositoryImpl(apiService, jobDao, ioDispatcher)
    }
}
