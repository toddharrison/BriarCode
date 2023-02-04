package com.briarcraft.econ.api.recipe

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.Recipe
import java.io.File

interface RecipeManager {
    fun getRecipe(key: NamespacedKey): Recipe?
    fun getRecipes(): Sequence<Recipe>
    fun getRecipesFor(type: Material): Sequence<Recipe>
    fun addRecipe(recipe: Recipe): Boolean
    fun removeRecipe(key: NamespacedKey): Boolean
    fun resetRecipes()
    fun clearRecipes()
    fun exportRecipes(targetJson: File)
}
