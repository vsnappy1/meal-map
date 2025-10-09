package com.randos.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.randos.data.database.util.LocalDateConverter
import com.randos.domain.type.MealType
import java.time.LocalDate

@Entity()
@TypeConverters(LocalDateConverter::class)
internal data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: MealType,
    val date: LocalDate,
)
