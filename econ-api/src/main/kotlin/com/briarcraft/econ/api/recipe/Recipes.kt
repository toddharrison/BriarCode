package com.briarcraft.econ.api.recipe

import com.briarcraft.kotlin.util.toEnumSet
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.*

val Recipe.key: NamespacedKey
    get() = getRecipeKey(this)
val Recipe.inputChoices: List<RecipeChoice>
    get() = getRecipeInputChoices(this)
val Recipe.inputChoiceItemStacks: List<List<ItemStack>>
    get() = getRecipeItemStacksMerged(this)

val RecipeChoice.itemStacks: List<ItemStack>
    get() = getRecipeChoiceItemStacks(this)
val RecipeChoice.types: Set<Material>
    get() = getRecipeChoiceMaterials(this)
val RecipeChoice.amount: Int
    get() = getRecipeChoiceAmount(this)



private fun getRecipeKey(recipe: Recipe): NamespacedKey = when (recipe) {
    is CookingRecipe<*> -> recipe.key
    is StonecuttingRecipe -> recipe.key
    is SmithingRecipe -> recipe.key
    is ShapedRecipe -> recipe.key
    is ShapelessRecipe -> recipe.key
    is ComplexRecipe -> recipe.key
    is ManualRecipe -> recipe.key
    is CachedRecipe -> recipe.key
    else -> throw IllegalArgumentException()
}

private fun getRecipeInputChoices(recipe: Recipe) = when (recipe) {
    is CookingRecipe<*> -> listOf(recipe.inputChoice)
    is StonecuttingRecipe -> listOf(recipe.inputChoice)
    is SmithingRecipe -> listOf(recipe.base, recipe.addition)
    is ShapedRecipe -> recipe.choiceMap.values.filterNotNull()
    is ShapelessRecipe -> recipe.choiceList
    is ComplexRecipe -> listOf()
    is ManualRecipe -> recipe.choiceList
    is CachedRecipe -> recipe.inputs.map { RecipeChoice.ExactChoice(it) }
    else -> throw IllegalArgumentException()
}

private fun getRecipeItemStacksMerged(recipe: Recipe) = recipe.inputChoices
    .map { it.types to it.amount }
    .groupBy { it.first }
    .map { (materials, counts) -> materials to counts.sumOf { it.second } }
    .map { (materials, count) -> materials.map { ItemStack(it, count) } }

private fun getRecipeChoiceItemStacks(recipeChoice: RecipeChoice) = when (recipeChoice) {
    is RecipeChoice.ExactChoice -> recipeChoice.choices
    is RecipeChoice.MaterialChoice -> recipeChoice.choices.map { ItemStack(it) }
    else -> throw IllegalArgumentException()
}

private fun getRecipeChoiceAmount(recipeChoice: RecipeChoice) = when (recipeChoice) {
    is RecipeChoice.ExactChoice -> recipeChoice.itemStack.amount
    is RecipeChoice.MaterialChoice -> 1
    else -> throw IllegalArgumentException()
}

private fun getRecipeChoiceMaterials(recipeChoice: RecipeChoice) = when (recipeChoice) {
    is RecipeChoice.ExactChoice -> recipeChoice.choices.map(ItemStack::getType).toEnumSet()
    is RecipeChoice.MaterialChoice -> recipeChoice.choices.toEnumSet()
    else -> throw IllegalArgumentException()
}
