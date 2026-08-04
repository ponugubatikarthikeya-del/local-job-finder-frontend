package com.linker.app.core.database.app_database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.linker.app.core.database.converters.Converters
import com.linker.app.core.database.dao.JobDao
import com.linker.app.core.database.dao.MyJobDao
import com.linker.app.core.database.dao.UserDao
import com.linker.app.core.database.entity.JobEntity
import com.linker.app.core.database.entity.MyJobEntity
import com.linker.app.core.database.entity.UserEntity

@Database(entities = [JobEntity::class, UserEntity::class, MyJobEntity::class], version = 7, exportSchema = false)
@TypeConverters(Converters::class)
abstract class JobDatabase : RoomDatabase() {
    abstract fun jobDao(): JobDao
    abstract fun userDao(): UserDao
    abstract fun myJobDao(): MyJobDao
}