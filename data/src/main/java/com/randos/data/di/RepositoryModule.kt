package com.randos.data.di

import com.randos.data.repository.IngredientRepositoryImpl
import com.randos.data.repository.MealRepositoryImpl
import com.randos.data.repository.RecipeRepositoryImpl
import com.randos.domain.repository.IngredientRepository
import com.randos.domain.repository.MealRepository
import com.randos.domain.repository.RecipeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds
    abstract fun bindIngredientRepository(impl: IngredientRepositoryImpl): IngredientRepository

    @Binds
    abstract fun bindRecipeRepository(impl: RecipeRepositoryImpl): RecipeRepository

    @Binds
    abstract fun bindMealRepository(impl: MealRepositoryImpl): MealRepository

}