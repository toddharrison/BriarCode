package com.briarcraft.adventure.api.event

import org.bukkit.event.Listener
import org.bukkit.inventory.Recipe
import org.bukkit.plugin.Plugin

interface RecipeEventListener: Listener {
    val plugin: Plugin

    fun createRecipe(): Recipe
}
