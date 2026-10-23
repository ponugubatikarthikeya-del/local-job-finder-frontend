package com.linker.app.core.database.di

import android.content.Context
import androidx.room.Room
import com.linker.app.core.database.app_database.JobDatabase
import com.linker.app.core.database.dao.ApplicantDao
import com.linker.app.core.database.dao.ApplicantRemoteDao
import com.linker.app.core.database.dao.JobDao
import com.linker.app.core.database.dao.JobRemoteDao
import com.linker.app.core.database.dao.MyJobDao
import com.linker.app.core.database.dao.MyJobsRemoteDao
import com.linker.app.core.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): JobDatabase {
        return Room.databaseBuilder(
            context,
            JobDatabase::class.java,
            "local_job_finder_db"
        ).fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideJobDao(database: JobDatabase): JobDao {
        return database.jobDao()
    }

    @Provides
    @Singleton
    fun provideJobRemoteDao(database: JobDatabase): JobRemoteDao {
        return database.jobRemoteDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: JobDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideMyJobDao(database: JobDatabase): MyJobDao {
        return database.myJobDao()
    }

    @Provides
    @Singleton
    fun provideMyJobRemoteDao(database: JobDatabase): MyJobsRemoteDao {
        return database.myJobRemoteDao()
    }

    @Provides
    @Singleton
    fun provideApplicantDao(database: JobDatabase): ApplicantDao {
        return database.applicantDao()
    }

    @Provides
    @Singleton
    fun provideApplicantRemoteDao(database: JobDatabase): ApplicantRemoteDao {
        return database.applicantRemoteDao()
    }
}
