package com.randos.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.randos.data.database.util.LocalDateConverter
import com.randos.data.database.util.RecipeTagListConverter
import com.randos.data.database.util.StringListConverter // Assuming this will be the path to your converter
import com.randos.domain.type.RecipeHeaviness
import com.randos.domain.type.RecipeTag
import java.time.LocalDate

@Entity()
@TypeConverters(
    StringListConverter::class,
    RecipeTagListConverter::class,
    LocalDateConverter::class
)
internal data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
    val description: String?,
    val imagePath: String?,
    val instructions: List<String>,
    val prepTime: Int?,
    val cookTime: Int?,
    val servings: Int?,
    val tags: List<RecipeTag>,
    val calories: Int?,
    val heaviness: RecipeHeaviness?,
    val dateCreated: LocalDate
)
