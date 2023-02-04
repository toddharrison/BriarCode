package com.briarcraft.econ.recipe

import com.briarcraft.econ.api.material.MaterialSet
import com.briarcraft.econ.api.recipe.RecipeService
import com.briarcraft.econ.api.recipe.inputChoiceItemStacks
import com.briarcraft.econ.api.recipe.key
import com.briarcraft.econ.material.CobbleReduceMaterials
import com.briarcraft.econ.material.QuartzReduceMaterials
import com.briarcraft.kotlin.util.toEnumMap
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

const val REDUCE_COBBLE = "reduce.cobblestone"
const val REDUCE_QUARTZ = "reduce.quartz"

fun getReduceMappings(
    recipeService: RecipeService,
    materialSets: Map<String, MaterialSet>? = null
): Map<Material, Map<Material, Double>> {
    val recipes = recipeService.recipeManager
    val manualRecipes = recipeService.manualRecipes
    val removedRecipes = recipeService.removedRecipes
    val removedKeys = removedRecipes.recipes.map { it.key }
    val usedRecipes = (recipes.getRecipes() + manualRecipes.recipes)
        .filterNot { removedKeys.contains(it.key) }
        .filter { recipeService.recipeNamespaces.contains(it.key.namespace) }

    val cobbleReduce = materialSets?.get(REDUCE_COBBLE) ?: CobbleReduceMaterials()
    val quartsReduce = materialSets?.get(REDUCE_QUARTZ) ?: QuartzReduceMaterials()

    return usedRecipes
        .filter { !it.result.type.isAir }
        .map { recipe -> recipe.result to recipe.inputChoiceItemStacks.map { determineBestChoice(cobbleReduce, quartsReduce, it) } }
        .filter { it.second.isNotEmpty() }
        .groupBy ({ it.first.type }) { it.first.amount to it.second }
        .mapValues { (_, inputs) -> determineBestRecipe(inputs) }
        .toEnumMap()
}



private fun determineBestChoice(cobbleReduce: MaterialSet, quartzReduce: MaterialSet, choices: List<ItemStack>): ItemStack {
    return if (choices.size == 1) choices.first()
    else when (choices.map { it.type }.toSet()) {
        cobbleReduce.types -> ItemStack(Material.COBBLESTONE, choices.first().amount)
        quartzReduce.types -> ItemStack(Material.QUARTZ_BLOCK, choices.first().amount)
        else -> choices.first()
    }
}

private fun determineBestRecipe(inputs: List<Pair<Int, List<ItemStack>>>): Map<Material, Double> {
    return inputs
        .map { (resultAmount, inputs) -> inputs
            .associate { it.type to it.amount.toDouble() / resultAmount } }
        .minBy { it.values.sum() }
}
