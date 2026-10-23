package com.linker.app.core.network_api.di

import com.linker.app.core.database.app_database.JobDatabase
import com.linker.app.core.database.dao.UserDao
import com.linker.app.core.datastore.TokenManager
import com.linker.app.core.network_api.api.ApplicationsApiService
import com.linker.app.core.network_api.api.AuthApiService
import com.linker.app.core.network_api.api.JobApiService
import com.linker.app.core.network_api.api.ProfileApiService
import com.linker.app.core.repository.ApplicationsRepository
import com.linker.app.core.repository.AuthRepository
import com.linker.app.core.repository.JobRepository
import com.linker.app.core.repository.ProfileRepository
import com.linker.app.core.repository_impl.ApplicationsRepositoryImpl
import com.linker.app.core.repository_impl.AuthRepositoryImpl
import com.linker.app.core.repository_impl.JobRepositoryImpl
import com.linker.app.core.repository_impl.ProfileRepositoryImpl
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
        applicationsApiService: ApplicationsApiService,
        database: JobDatabase,
        ioDispatcher: CoroutineDispatcher
    ): JobRepository {
        return JobRepositoryImpl(apiService, applicationsApiService, database, ioDispatcher)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        apiService: ProfileApiService,
        userDao: UserDao,
        ioDispatcher: CoroutineDispatcher
    ): ProfileRepository {
        return ProfileRepositoryImpl(apiService, userDao, ioDispatcher)
    }

    @Provides
    @Singleton
    fun provideApplicationsRepository(
        apiService: ApplicationsApiService,
        database: JobDatabase,
        ioDispatcher: CoroutineDispatcher
    ): ApplicationsRepository {
        return ApplicationsRepositoryImpl(apiService, database, ioDispatcher)
    }
}
