package com.briarcraft.econ.recipe

import com.briarcraft.econ.api.recipe.CachedRecipe
import com.briarcraft.econ.api.recipe.RecipeManager
import com.briarcraft.econ.api.recipe.key
import com.briarcraft.econ.util.gsonWithExcludes
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Server
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import java.io.File
import java.io.FileWriter

class ServerRecipeManager(private val server: Server): RecipeManager {
    override fun getRecipe(key: NamespacedKey) = server.getRecipe(key)
    override fun getRecipes() = server.recipeIterator().asSequence()
    override fun getRecipesFor(type: Material) = server.getRecipesFor(ItemStack(type)).asSequence()
    override fun addRecipe(recipe: Recipe) = server.addRecipe(recipe)
    override fun removeRecipe(key: NamespacedKey) = server.removeRecipe(key)
    override fun resetRecipes() = server.resetRecipes()
    override fun clearRecipes() = server.clearRecipes()

    override fun exportRecipes(targetJson: File) {
        FileWriter(targetJson).use { writer ->
            gsonWithExcludes().setPrettyPrinting().create().toJson(
                getRecipes()
                    .map { recipe -> recipe.key.toString() to CachedRecipe(recipe) }
                    .sortedBy { pair -> pair.first }
                    .toMap(),
                writer)
        }
    }
}