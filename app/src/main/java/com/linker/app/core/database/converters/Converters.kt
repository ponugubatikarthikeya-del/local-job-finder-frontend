package com.linker.app.core.database.converters

import androidx.room.TypeConverter
import com.linker.app.core.util.enums.JobStatusEnum
import com.linker.app.core.util.enums.JobTypeEnum
import java.math.BigDecimal

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
}