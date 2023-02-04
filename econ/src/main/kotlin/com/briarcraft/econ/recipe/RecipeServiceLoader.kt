package com.briarcraft.econ.recipe

import com.briarcraft.econ.api.recipe.ManualRecipe
import com.briarcraft.econ.api.recipe.RecipeManager
import com.briarcraft.econ.api.recipe.RecipeService
import com.briarcraft.econ.api.recipe.RecipeSet
import org.bukkit.Material
import org.bukkit.configuration.Configuration
import org.bukkit.plugin.Plugin

const val RECIPES_NAMESPACES = "namespaces"
const val RECIPES_REMOVED = "removed"
const val RECIPES_MANUAL = "manual"

fun loadRecipeService(
    plugin: Plugin,
    config: Configuration,
    recipeManager: RecipeManager = ServerRecipeManager(plugin.server)
): RecipeService {
    val recipeNamespaces = config.getStringList(RECIPES_NAMESPACES)

    val recipeSets: MutableMap<String, RecipeSet> = config.getKeys(true).associateWith { key ->
        ConfigRecipes(config, key, recipeManager)
    }.filter { it.value.recipes.isNotEmpty() }
        .toMutableMap()

    val manualRecipes = config.getConfigurationSection(RECIPES_MANUAL)?.getKeys(false)?.mapNotNull { key ->
        val output = Material.getMaterial(key)
        val inputs = config.getString("$RECIPES_MANUAL.$key")
            ?.split(",")
            ?.map { it.trim() }
            ?.mapNotNull(Material::getMaterial)
            ?.toTypedArray()
        if (output != null && inputs != null) {
            ManualRecipe(output, *inputs)
        } else null
    }
        ?.toSet()
        ?.let { RecipeSetImpl(it) } ?: ManualRecipes()
    recipeSets[RECIPES_MANUAL] = manualRecipes

    val removedRecipes = recipeSets[RECIPES_REMOVED] ?: RemovedRecipes(recipeManager)

    return RecipeServiceImpl(plugin, recipeNamespaces, recipeManager, recipeSets, manualRecipes, removedRecipes)
}
