package com.randos.data.mapper

import com.randos.domain.model.Ingredient
import com.randos.domain.model.Meal
import com.randos.domain.model.Recipe
import com.randos.domain.model.RecipeIngredient

internal fun Ingredient.toEntity(): com.randos.data.database.entity.Ingredient =
    com.randos.data.database.entity.Ingredient(
        id = id,
        name = name
    )

internal fun com.randos.data.database.entity.Ingredient.toDomain(): Ingredient = Ingredient(
    id = id,
    name = name
)

internal fun Recipe.toEntity(): com.randos.data.database.entity.Recipe = com.randos.data.database.entity.Recipe(
    id = id,
    title = title,
    description = description,
    imagePath = imagePath,
    instructions = instructions,
    prepTime = prepTime,
    cookTime = cookTime,
    servings = servings,
    tags = tags,
    calories = calories,
    heaviness = heaviness,
    dateCreated = dateCreated
)

internal fun com.randos.data.database.entity.Recipe.toDomain(ingredients: List<RecipeIngredient>): Recipe = Recipe(
    id = id,
    title = title,
    description = description,
    imagePath = imagePath,
    instructions = instructions,
    ingredients = ingredients,
    prepTime = prepTime,
    cookTime = cookTime,
    servings = servings,
    tags = tags,
    calories = calories,
    heaviness = heaviness,
    dateCreated = dateCreated
)

internal fun RecipeIngredient.toEntity(
    recipeId: Long,
    ingredientId: Long
): com.randos.data.database.entity.RecipeIngredient = com.randos.data.database.entity.RecipeIngredient(
    id = 0,
    recipeId = recipeId,
    ingredientId = ingredientId,
    quantity = quantity,
    unit = unit
)

internal fun com.randos.data.database.entity.RecipeIngredient.toDomain(ingredient: Ingredient): RecipeIngredient =
    RecipeIngredient(
        ingredient = ingredient,
        quantity = quantity,
        unit = unit
    )

internal fun Meal.toEntity(): com.randos.data.database.entity.Meal = com.randos.data.database.entity.Meal(
    id = id,
    type = type,
    date = date
)

internal fun com.randos.data.database.entity.Meal.toDomain(recipes: List<Recipe>): Meal = Meal(
    id = id,
    type = type,
    date = date,
    recipes = recipes
)
