package com.randos.data.database.util

import androidx.room.TypeConverter
import java.util.Date

internal class DateConverter {

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        if (date == null) {
            return null
        }
        return date.time
    }

    @TypeConverter
    fun fromLong(millisSinceEpoch: Long?): Date? {
        if (millisSinceEpoch == null) {
            return null
        }
        return Date(millisSinceEpoch)
    }
}