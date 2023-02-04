package com.briarcraft.econ.material

import com.briarcraft.econ.api.material.MaterialService
import com.briarcraft.econ.api.material.MaterialSet
import com.briarcraft.econ.api.recipe.RecipeService
import com.briarcraft.econ.recipe.getReduceMappings
import org.bukkit.Material
import org.bukkit.plugin.Plugin

class MaterialServiceImpl(
    override val plugin: Plugin,
    recipeService: RecipeService,
    override val materialSets: Map<String, MaterialSet> = mapOf(),
    invalidItems: MaterialSet = InvalidItemMaterials(),
    unavailableItems: MaterialSet = UnavailableItemMaterials(),
    override val excludedItems: MaterialSet = ExcludedItemMaterials(),
    override val allItems: MaterialSet = ItemMaterials(invalidItems, unavailableItems),
    override val baseItems: MaterialSet = BaseItemMaterials(allItems, excludedItems, recipeService),
    override val reduceItems: Map<Material, Map<Material, Double>> = getReduceMappings(recipeService, materialSets)
): MaterialService
