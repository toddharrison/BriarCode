package com.briarcraft.econ.api.recipe

import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority

interface RecipeService {
    val plugin: Plugin
    val recipeNamespaces: List<String>
    val recipeManager: RecipeManager
    val manualRecipes: RecipeSet
    val removedRecipes: RecipeSet
    val recipeSets: Map<String, RecipeSet>

    fun registerService() = plugin.server.servicesManager.let { servicesManager ->
        if (!servicesManager.isProvidedFor(RecipeService::class.java)) {
            servicesManager.register(RecipeService::class.java, this, plugin, ServicePriority.Normal)
            true
        } else false
    }
}
