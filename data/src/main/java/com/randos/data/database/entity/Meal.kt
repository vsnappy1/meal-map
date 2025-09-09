package com.randos.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.randos.data.database.util.DateConverter
import com.randos.domain.type.MealType
import java.util.Date

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = MealPlan::class,
            parentColumns = ["id"],
            childColumns = ["meal_plan_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@TypeConverters(DateConverter::class)
internal data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: MealType,
    val date: Date,

    @ColumnInfo(name = "meal_plan_id", index = true)
    val mealPlanId: Long
)
