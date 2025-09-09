package com.randos.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["meal_id", "recipe_id"],
    foreignKeys = [
        ForeignKey(
            entity = Meal::class,
            parentColumns = ["id"],
            childColumns = ["meal_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipe_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
internal data class MealRecipeCrossRef(
    @ColumnInfo(name = "meal_id", index = true)
    val mealId: Long,
    @ColumnInfo(name = "recipe_id", index = true)
    val recipeId: Long
)
