package com.randos.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.randos.domain.type.IngredientUnit

@Entity()
internal data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val unit: IngredientUnit,
    val calories: Int
)
