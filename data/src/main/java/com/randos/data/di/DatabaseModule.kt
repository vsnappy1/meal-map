package com.randos.data.di

import android.app.Application
import androidx.room.Room
import com.randos.data.database.MealMapDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    fun provideMealMapDatabase(application: Application): MealMapDatabase {
        return Room.databaseBuilder(
            application,
            MealMapDatabase::class.java, "meal_map_database"
        ).build()
    }

    @Provides
    fun provideIngredientDao(database: MealMapDatabase) = database.ingredientDao()

    @Provides
    fun provideRecipeIngredientDao(database: MealMapDatabase) = database.recipeIngredientDao()

    @Provides
    fun provideRecipeDao(database: MealMapDatabase) = database.recipeDao()

    @Provides
    fun provideMealDao(database: MealMapDatabase) = database.mealDao()

    @Provides
    fun MealRecipeCrossRefDao(database: MealMapDatabase) = database.mealRecipeCrossRefDao()
}