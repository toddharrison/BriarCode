package com.briarcraft.econ.material

import com.briarcraft.econ.api.material.*
import com.briarcraft.econ.recipe.CacheRecipeManager
import com.briarcraft.econ.recipe.RecipeServiceImpl
import org.bukkit.plugin.Plugin
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import java.io.File

class MaterialSetTest {
    @Nested
    inner class BaseItemMaterialsTest {
        private val plugin = mock<Plugin>()

        private lateinit var materials: MaterialSet

        private val recipeManager = CacheRecipeManager(File("src/test/resources/recipes.json"))
            .also { it.loadRecipes() }
        private val items = ItemMaterials(InvalidItemMaterials(), UnavailableItemMaterials())
        private val excludedItems = ExcludedItemMaterials()
        private val recipeService = RecipeServiceImpl(plugin, listOf("minecraft", "manual"), recipeManager)

        @BeforeEach
        fun setup() {
            materials = BaseItemMaterials(items, excludedItems, recipeService)
        }

        @Test
        fun `call test`() {
            // Act
            val response = materials.types

            // Assert
            Assertions.assertEquals(283, response.size)
        }
    }
}