package com.linker.app.core.database.app_database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.linker.app.core.database.converters.Converters
import com.linker.app.core.database.dao.JobDao
import com.linker.app.core.database.entity.JobEntity

@Database(entities = [JobEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class JobDatabase : RoomDatabase() {
    abstract fun jobDao(): JobDao
}