package com.briarcraft.econ.recipe

import com.briarcraft.econ.api.recipe.RecipeManager
import com.briarcraft.econ.api.recipe.RecipeService
import com.briarcraft.econ.api.recipe.RecipeSet
import org.bukkit.plugin.Plugin

class RecipeServiceImpl(
    override val plugin: Plugin,
    override val recipeNamespaces: List<String>,
    override val recipeManager: RecipeManager,
    override val recipeSets: Map<String, RecipeSet> = mapOf(),
    override val manualRecipes: RecipeSet = ManualRecipes(),
    override val removedRecipes: RecipeSet = RemovedRecipes(recipeManager),
): RecipeService
