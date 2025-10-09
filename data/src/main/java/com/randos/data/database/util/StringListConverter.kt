package com.randos.data.database.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private val listType = object : TypeToken<List<String>>() {}.type

internal class StringListConverter {
    @TypeConverter
    fun fromString(value: String?): List<String>? {
        return value?.let { Gson().fromJson(it, listType) }
    }

    @TypeConverter
    fun fromList(list: List<String>?): String? {
        return list?.let { Gson().toJson(it) }
    }
}
