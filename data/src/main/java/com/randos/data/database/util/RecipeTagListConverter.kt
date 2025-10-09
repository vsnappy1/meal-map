package com.randos.data.database.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.randos.domain.type.RecipeTag

private val recipeTagListType = object : TypeToken<List<RecipeTag>>() {}.type

internal class RecipeTagListConverter {
    @TypeConverter
    fun fromString(value: String?): List<RecipeTag>? {
        return value?.let { Gson().fromJson(it, recipeTagListType) }
    }

    @TypeConverter
    fun fromList(list: List<RecipeTag>?): String? {
        return list?.let { Gson().toJson(it) }
    }
}
