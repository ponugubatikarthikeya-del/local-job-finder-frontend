package com.linker.app.core.database.di

import android.content.Context
import androidx.room.Room
import com.linker.app.core.database.app_database.JobDatabase
import com.linker.app.core.database.dao.JobDao
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
        ).build()
    }

    @Provides
    @Singleton
    fun provideJobDao(database: JobDatabase): JobDao {
        return database.jobDao()
    }
}
