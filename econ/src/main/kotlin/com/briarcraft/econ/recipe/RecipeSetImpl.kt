package com.briarcraft.econ.recipe

import com.briarcraft.econ.api.recipe.RecipeSet
import org.bukkit.inventory.Recipe

class RecipeSetImpl(
    override val recipes: Set<Recipe>
): RecipeSet {
}
