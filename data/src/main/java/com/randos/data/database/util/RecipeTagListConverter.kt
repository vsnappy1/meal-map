package com.randos.data.database.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.randos.domain.type.RecipeTag

internal class RecipeTagListConverter {
    @TypeConverter
    fun fromString(value: String?): List<RecipeTag>? {
        if (value == null) {
            return null
        }
        val listType = object : TypeToken<List<RecipeTag>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<RecipeTag>?): String? {
        if (list == null) {
            return null
        }
        return Gson().toJson(list)
    }
}
