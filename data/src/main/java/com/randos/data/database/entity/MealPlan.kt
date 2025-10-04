package com.randos.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
internal data class MealPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fromDate: LocalDate,
    val toDate: LocalDate,
)
