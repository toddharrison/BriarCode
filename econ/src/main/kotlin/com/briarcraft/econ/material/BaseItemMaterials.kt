package com.briarcraft.econ.material

import com.briarcraft.econ.api.material.MaterialSet
import com.briarcraft.econ.api.recipe.*
import com.briarcraft.kotlin.util.toEnumSet
import org.bukkit.Material

class BaseItemMaterials(
    allItems: MaterialSet,
    excludedItems: MaterialSet,
    recipeService: RecipeService
): MaterialSet {
    override val types: Set<Material>

    init {
        val recipes = recipeService.recipeManager
        val removedRecipes = recipeService.removedRecipes
        val manualRecipes = recipeService.manualRecipes
        val removedKeys = removedRecipes.recipes.map { it.key }
        val usedRecipes = (recipes.getRecipes() + manualRecipes.recipes)
            .filterNot { removedKeys.contains(it.key) }
        val craftedTypes = usedRecipes
            .map { recipe -> recipe.result.type }
            .toEnumSet()

        types = allItems.types - craftedTypes - excludedItems.types
    }
}
