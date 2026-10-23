package com.linker.app.core.database.app_database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.linker.app.core.database.converters.Converters
import com.linker.app.core.database.dao.ApplicantDao
import com.linker.app.core.database.dao.ApplicantRemoteDao
import com.linker.app.core.database.dao.JobDao
import com.linker.app.core.database.dao.JobRemoteDao
import com.linker.app.core.database.dao.MyJobDao
import com.linker.app.core.database.dao.MyJobsRemoteDao
import com.linker.app.core.database.dao.UserDao
import com.linker.app.core.database.entity.ApplicantEntity
import com.linker.app.core.database.entity.ApplicantRemoteEntity
import com.linker.app.core.database.entity.JobEntity
import com.linker.app.core.database.entity.JobRemoteEntity
import com.linker.app.core.database.entity.MyJobEntity
import com.linker.app.core.database.entity.MyJobRemoteEntity
import com.linker.app.core.database.entity.UserEntity

@Database(
    entities = [
        JobEntity::class, 
        UserEntity::class, 
        MyJobEntity::class, 
        ApplicantEntity::class,
        JobRemoteEntity::class,
        MyJobRemoteEntity::class,
        ApplicantRemoteEntity::class
    ], 
    version = 13, 
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class JobDatabase : RoomDatabase() {
    abstract fun jobDao(): JobDao
    abstract fun jobRemoteDao(): JobRemoteDao
    abstract fun userDao(): UserDao
    abstract fun myJobDao(): MyJobDao
    abstract fun myJobRemoteDao(): MyJobsRemoteDao
    abstract fun applicantDao(): ApplicantDao
    abstract fun applicantRemoteDao(): ApplicantRemoteDao
}
