package com.briarcraft.econ.recipe

import com.briarcraft.econ.api.recipe.CachedRecipe
import com.briarcraft.econ.api.recipe.RecipeManager
import com.briarcraft.econ.api.recipe.key
import com.briarcraft.econ.util.gsonWithExcludes
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.Recipe
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.HashMap

class CacheRecipeManager(private val sourceJson: File): RecipeManager {
    private val recipes: RecipeMap = RecipeMap()

    fun loadRecipes(): CacheRecipeManager {
        FileReader(sourceJson).use {
            recipes.clear()
            recipes.putAll(gsonWithExcludes().create().fromJson(it, RecipeMap::class.java))
        }
        return this
    }

    fun loadRecipes(recipeModel: RecipeManager) {
        recipes.clear()
        recipes.putAll(recipeModel.getRecipes().map { it.key.toString() to CachedRecipe(it) }.toMap())
    }

    override fun getRecipe(key: NamespacedKey) = recipes[key.toString()]
    override fun getRecipes() = recipes.values.asSequence()
    override fun getRecipesFor(type: Material) = recipes.values.asSequence()
        .filter { it.output.type == type }
    override fun addRecipe(recipe: Recipe) = CachedRecipe(recipe).let { recipes.put(it.key.toString(), it) }.let { true }
    override fun removeRecipe(key: NamespacedKey) = recipes.remove(key.toString()) != null
    override fun resetRecipes() { loadRecipes() }
    override fun clearRecipes() = recipes.clear()
    fun exportRecipes() = exportRecipes(sourceJson)
    override fun exportRecipes(targetJson: File) {
        FileWriter(targetJson).use {
            gsonWithExcludes().setPrettyPrinting().create().toJson(recipes.toSortedMap(), it)
        }
    }

    class RecipeMap: HashMap<String, CachedRecipe>()
}