package com.randos.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
internal data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
)
