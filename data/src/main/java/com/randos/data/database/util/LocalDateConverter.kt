package com.randos.data.database.util

import androidx.room.TypeConverter
import java.time.LocalDate

internal class LocalDateConverter {
    @TypeConverter
    fun fromDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromString(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }
}