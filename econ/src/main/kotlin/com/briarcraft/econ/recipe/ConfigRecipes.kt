package com.briarcraft.econ.recipe

import com.briarcraft.econ.api.recipe.RecipeManager
import com.briarcraft.econ.api.recipe.RecipeSet
import org.bukkit.NamespacedKey
import org.bukkit.configuration.Configuration
import org.bukkit.inventory.Recipe

class ConfigRecipes(
    config: Configuration,
    val name: String,
    private val recipeModel: RecipeManager
): RecipeSet {
    override val recipes: Set<Recipe> = config.getStringList("$name.recipes")
        .mapNotNull(NamespacedKey::fromString)
        .mapNotNull(recipeModel::getRecipe)
        .toSet()
}
