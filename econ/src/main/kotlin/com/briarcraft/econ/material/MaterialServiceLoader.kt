package com.briarcraft.econ.material

import com.briarcraft.econ.api.material.MaterialService
import com.briarcraft.econ.api.recipe.RecipeService
import com.briarcraft.kotlin.util.toEnumSet
import org.bukkit.Material
import org.bukkit.configuration.Configuration
import org.bukkit.plugin.Plugin

const val MATERIALS_INVALID = "invalid"
const val MATERIALS_UNAVAILABLE = "unavailable"
const val MATERIALS_EXCLUDED = "excluded"

fun loadMaterialService(plugin: Plugin, recipeService: RecipeService, config: Configuration): MaterialService {
    val materialSets = config.getKeys(true).associateWith { key ->
        config.getStringList("$key.types")
            .mapNotNull(Material::getMaterial)
            .let { if (it.size > 16) it.toSet() else it.toEnumSet() }
            .let { MaterialSetImpl(it) }
    }.filterValues { it.types.isNotEmpty() }

    val invalidItems = materialSets[MATERIALS_INVALID] ?: InvalidItemMaterials()
    val unavailableItems = materialSets[MATERIALS_UNAVAILABLE] ?: UnavailableItemMaterials()
    val excludedItems = materialSets[MATERIALS_EXCLUDED] ?: ExcludedItemMaterials()

    return MaterialServiceImpl(plugin, recipeService, materialSets, invalidItems, unavailableItems, excludedItems)
}
