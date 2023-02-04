package com.briarcraft.econ.stock

import com.briarcraft.econ.api.stock.StockAmount
import com.briarcraft.econ.material.*
import com.briarcraft.econ.recipe.CacheRecipeManager
import com.briarcraft.econ.recipe.RecipeServiceImpl
import com.briarcraft.econ.recipe.getReduceMappings
import com.briarcraft.kotlin.util.*
import org.bukkit.Material
import org.bukkit.plugin.Plugin
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import org.mockito.kotlin.mock
import java.io.File

class ReducingStockManagerTest {
    @Nested
    inner class ReducingStockModelTest {
        private val plugin = mock<Plugin>()

        private val recipeManager = CacheRecipeManager(File("src/test/resources/recipes.json"))
            .also { it.loadRecipes() }
        val items = ItemMaterials(InvalidItemMaterials(), UnavailableItemMaterials())
        val excludedItems = ExcludedItemMaterials()
        val recipeService = RecipeServiceImpl(plugin, listOf("minecraft", "manual"), recipeManager)
        val materials = BaseItemMaterials(items, excludedItems, recipeService)
        val reduceItems: Map<Material, Map<Material, Double>> = getReduceMappings(recipeService)
        val baseItems = materials.types.associateWith { _ -> StockAmount(0.0, 100_000.0) }

        private lateinit var stock: BaseItemReducingStockManager

        @BeforeEach
        fun setup() {
            stock = BaseItemReducingStockManager(reduceItems, baseItems)
        }

        @Nested
        inner class SimplifyItemsTest {
            @ParameterizedTest
            @CsvFileSource(resources = ["/itemToReduce1.csv"] )
            fun `process data`(inTypeName: String, outTypeName: String, outAmount: Double) {
                // Arrange
                val items = enumMapOf(Material.getMaterial(inTypeName)!! to 1.0)

                // Act
                val response = stock.reduce(items)

                // Assert
                assertEquals(enumMapOf(
                    Material.getMaterial(outTypeName)!! to outAmount
                ), response)
            }

            @ParameterizedTest
            @CsvFileSource(resources = ["/itemToReduce2.csv"] )
            fun `process data`(inTypeName: String, outTypeName1: String, outAmount1: Double, outTypeName2: String, outAmount2: Double) {
                // Arrange
                val items = enumMapOf(Material.getMaterial(inTypeName)!! to 1.0)

                // Act
                val response = stock.reduce(items)

                // Assert
                assertEquals(enumMapOf(
                    Material.getMaterial(outTypeName1)!! to outAmount1,
                    Material.getMaterial(outTypeName2)!! to outAmount2,
                ), response)
            }

            @ParameterizedTest
            @CsvFileSource(resources = ["/itemToReduce3.csv"] )
            fun `process data`(inTypeName: String, outTypeName1: String, outAmount1: Double, outTypeName2: String, outAmount2: Double, outTypeName3: String, outAmount3: Double) {
                // Arrange
                val items = enumMapOf(Material.getMaterial(inTypeName)!! to 1.0)

                // Act
                val response = stock.reduce(items)

                // Assert
                assertEquals(enumMapOf(
                    Material.getMaterial(outTypeName1)!! to outAmount1,
                    Material.getMaterial(outTypeName2)!! to outAmount2,
                    Material.getMaterial(outTypeName3)!! to outAmount3,
                ), response)
            }

            @ParameterizedTest
            @CsvFileSource(resources = ["/itemToReduce4.csv"] )
            fun `process data`(inTypeName: String, outTypeName1: String, outAmount1: Double, outTypeName2: String, outAmount2: Double, outTypeName3: String, outAmount3: Double, outTypeName4: String, outAmount4: Double) {
                // Arrange
                val items = enumMapOf(Material.getMaterial(inTypeName)!! to 1.0)

                // Act
                val response = stock.reduce(items)

                // Assert
                assertEquals(enumMapOf(
                    Material.getMaterial(outTypeName1)!! to outAmount1,
                    Material.getMaterial(outTypeName2)!! to outAmount2,
                    Material.getMaterial(outTypeName3)!! to outAmount3,
                    Material.getMaterial(outTypeName4)!! to outAmount4,
                ), response)
            }

            @ParameterizedTest
            @CsvFileSource(resources = ["/itemToReduce5.csv"] )
            fun `process data`(inTypeName: String, outTypeName1: String, outAmount1: Double, outTypeName2: String, outAmount2: Double, outTypeName3: String, outAmount3: Double, outTypeName4: String, outAmount4: Double, outTypeName5: String, outAmount5: Double) {
                // Arrange
                val items = enumMapOf(Material.getMaterial(inTypeName)!! to 1.0)

                // Act
                val response = stock.reduce(items)

                // Assert
                assertEquals(enumMapOf(
                    Material.getMaterial(outTypeName1)!! to outAmount1,
                    Material.getMaterial(outTypeName2)!! to outAmount2,
                    Material.getMaterial(outTypeName3)!! to outAmount3,
                    Material.getMaterial(outTypeName4)!! to outAmount4,
                    Material.getMaterial(outTypeName5)!! to outAmount5,
                ), response)
            }
        }
    }
}
