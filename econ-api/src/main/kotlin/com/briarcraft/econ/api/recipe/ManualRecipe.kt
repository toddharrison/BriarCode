package com.briarcraft.econ.api.recipe

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice

class ManualRecipe(
    private val output: Material,
    private vararg val inputs: Material,
    keyNamespace: String = "manual",
    keyValue: String = "${output.name.lowercase()}_from_${inputs.joinToString("_and_", transform = { it.name.lowercase() })}"
): Recipe {
    private val result = ItemStack(output)

    val key = NamespacedKey(keyNamespace, keyValue)
    val choiceList: List<RecipeChoice>
        get() = inputs.map(RecipeChoice::MaterialChoice)
    val inputChoices: List<List<ItemStack>>
        get() = inputs.map { listOf(ItemStack(it)) }.toList()

    override fun getResult() = result

    override fun toString() = key.toString()
}
