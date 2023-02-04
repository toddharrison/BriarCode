package com.briarcraft.econ.api.recipe

import org.bukkit.NamespacedKey
import org.bukkit.inventory.Recipe

class CachedRecipe(
    val key: NamespacedKey,
    val inputs: List<List<CachedItemStack>>,
    val output: CachedItemStack
): Recipe {
    constructor(recipe: Recipe): this(
        recipe.key,
        recipe.inputChoiceItemStacks
            .map { itemStacks -> itemStacks.map { CachedItemStack(it) } },
        CachedItemStack(recipe.result)
    )

    override fun getResult() = output

    override fun toString() = "CachedRecipe(key=$key, inputs=$inputs, output=$output)"
}
