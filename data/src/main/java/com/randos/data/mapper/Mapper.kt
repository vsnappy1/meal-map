package com.randos.data.mapper

import com.randos.domain.model.Ingredient
import com.randos.domain.model.Meal
import com.randos.domain.model.Recipe
import com.randos.domain.model.RecipeIngredient

internal fun Ingredient.toEntity(): com.randos.data.database.entity.Ingredient {
    return com.randos.data.database.entity.Ingredient(
        id = id,
        name = name
    )
}

internal fun com.randos.data.database.entity.Ingredient.toDomain(): Ingredient {
    return Ingredient(
        id = id,
        name = name
    )
}

internal fun Recipe.toEntity(): com.randos.data.database.entity.Recipe {
    return com.randos.data.database.entity.Recipe(
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
}

internal fun com.randos.data.database.entity.Recipe.toDomain(ingredients: List<RecipeIngredient>): Recipe{
    return Recipe(
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
}

internal fun RecipeIngredient.toEntity(recipeId: Long, ingredientId: Long): com.randos.data.database.entity.RecipeIngredient {
    return com.randos.data.database.entity.RecipeIngredient(
        recipeId = recipeId,
        ingredientId = ingredientId,
        quantity = quantity,
        unit = unit,
    )
}

internal fun com.randos.data.database.entity.RecipeIngredient.toDomain(ingredient: Ingredient): RecipeIngredient {
    return RecipeIngredient(
        ingredient = ingredient,
        quantity = quantity,
        unit = unit,
    )
}

internal fun Meal.toEntity(): com.randos.data.database.entity.Meal {
    return com.randos.data.database.entity.Meal(
        id = id,
        type = type,
        date = date,
    )
}

internal fun com.randos.data.database.entity.Meal.toDomain(recipes: List<Recipe>): Meal {
    return Meal(
        id = id,
        type = type,
        date = date,
        recipes = recipes
    )
}