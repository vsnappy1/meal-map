package com.randos.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["name"], unique = true)])
internal data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String
)
