package com.briarcraft.econ.recipe

import com.briarcraft.econ.api.recipe.inputChoiceItemStacks
import com.briarcraft.kotlin.util.itemStackOf
import org.bukkit.Material
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapelessRecipe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class RecipesTest {
    @Nested
    inner class InputChoiceItemStacksTest {
        private val recipe = mock<ShapelessRecipe>()

        @Test
        fun `one option`() {
            // Arrange
            val choiceList = listOf(
                RecipeChoice.ExactChoice(itemStackOf(Material.PACKED_MUD)),
                RecipeChoice.ExactChoice(itemStackOf(Material.WHITE_CONCRETE_POWDER)),
            )

            whenever(recipe.choiceList).thenReturn(choiceList)

            // Act
            val response = recipe.inputChoiceItemStacks

            // Assert
            assertEquals("[[PACKED_MUD:1], [WHITE_CONCRETE_POWDER:1]]",
                response.map { list -> list.map { "${it.type}:${it.amount}" } }.toString())
        }

        @Test
        fun `two options`() {
            // Arrange
            val choiceList = listOf(
                RecipeChoice.ExactChoice(itemStackOf(Material.PACKED_MUD)),
                RecipeChoice.MaterialChoice(Material.WHITE_CONCRETE_POWDER, Material.BROWN_CONCRETE_POWDER)
            )

            whenever(recipe.choiceList).thenReturn(choiceList)

            // Act
            val response = recipe.inputChoiceItemStacks

            // Assert
            assertEquals("[[PACKED_MUD:1], [WHITE_CONCRETE_POWDER:1, BROWN_CONCRETE_POWDER:1]]",
                response.map { list -> list.map { "${it.type}:${it.amount}" } }.toString())
        }
    }
}
