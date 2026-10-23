package com.linker.app.core.database.converters

import androidx.room.TypeConverter
import com.linker.app.core.util.enums.JobStatusEnum
import com.linker.app.core.util.enums.JobTypeEnum
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime

class Converters {
    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? {
        return value?.let { BigDecimal(it) }
    }

    @TypeConverter
    fun fromJobType(value: JobTypeEnum): String {
        return value.name
    }

    @TypeConverter
    fun toJobType(value: String): JobTypeEnum {
        return JobTypeEnum.valueOf(value)
    }

    @TypeConverter
    fun fromJobStatus(value: JobStatusEnum): String {
        return value.name
    }

    @TypeConverter
    fun toJobStatus(value: String): JobStatusEnum {
        return JobStatusEnum.valueOf(value)
    }

    @TypeConverter
    fun fromInstant(value: Instant?): Long? {
        return value?.toEpochMilli()
    }

    @TypeConverter
    fun toInstant(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.joinToString("|||")
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.split("|||")?.filter { it.isNotEmpty() }
    }
}
