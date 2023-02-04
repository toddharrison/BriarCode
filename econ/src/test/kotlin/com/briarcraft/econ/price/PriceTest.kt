package com.briarcraft.econ.price

import com.briarcraft.econ.api.item.ItemAmount
import com.briarcraft.econ.api.price.*
import com.briarcraft.econ.api.stock.ReducingStockManager
import com.briarcraft.econ.api.stock.Stock
import com.briarcraft.econ.api.stock.StockManager
import com.briarcraft.kotlin.util.itemStackOf
import org.bukkit.Material
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PriceTest {
    @Nested
    inner class FreePricingTest {
        private val stockModel = mock<StockManager>()

        private lateinit var model: FreePricingManager

        @BeforeEach
        fun setup() {
            model = FreePricingManager(stockModel)
        }

        @Nested
        inner class GetPriceTest {
            @Test
            fun `call with item in stock`() {
                // Arrange
                whenever(stockModel.getItems()).thenReturn(setOf(Material.STICK))

                // Act
                val response = model.getAmountPrice(ItemAmount(Material.STICK, 3.0))

                // Assert
                assertEquals(0.0, response)
            }

            @Test
            fun `call with item in stock and price adjustment`() {
                // Arrange
                val priceAdjustment = PriceAdjustment(1.5, 0.5)
                whenever(stockModel.getItems()).thenReturn(setOf(Material.STICK))

                // Act
                val response = model.getAmountPrice(ItemAmount(Material.STICK, 3.0), priceAdjustment)

                // Assert
                assertEquals(0.0, response)
            }

            @Test
            fun `call with item not in stock`() {
                // Arrange
                whenever(stockModel.getItems()).thenReturn(setOf(Material.OAK_LOG))

                // Act
                val response = model.getAmountPrice(ItemAmount(Material.STICK, 3.0))

                // Assert
                assertNull(response)
            }

            @Test
            fun `call with multiple same items`() {
                // Arrange
                whenever(stockModel.getItems()).thenReturn(setOf(Material.STICK))

                // Act
                val response = model.getStackPrice(listOf(
                    itemStackOf(Material.STICK, amount = 3),
                    itemStackOf(Material.STICK, amount = 2)))

                // Assert
                assertEquals(0.0, response)
            }
        }
    }

    @Nested
    inner class StaticPricingTest {
        private val stockModel = mock<StockManager>()

        private lateinit var model: StaticPricingManager

        @BeforeEach
        fun setup() {
            whenever(stockModel.getItems()).thenReturn(setOf(Material.STICK))

            model = StaticPricingManager(
                stockModel, mapOf(
                    Material.STICK to 4.0
                )
            )
        }

        @Nested
        inner class GetPriceTest {
            @Test
            fun `call with item in stock`() {
                // Act
                val response = model.getAmountPrice(ItemAmount(Material.STICK, 3.0))

                // Assert
                Assertions.assertEquals(12.0, response)
            }

            @Test
            fun `call with item in stock and price adjustment`() {
                // Arrange
                val priceAdjustment = PriceAdjustment(1.5, 0.5)

                // Act
                val response = model.getAmountPrice(ItemAmount(Material.STICK, 3.0), priceAdjustment)

                // Assert
                Assertions.assertEquals(24.0, response)
            }

            @Test
            fun `call with item not in stock`() {
                // Act
                val response = model.getAmountPrice(ItemAmount(Material.OAK_LOG, 3.0))

                // Assert
                Assertions.assertNull(response)
            }

            @Test
            fun `call with multiple same items`() {
                // Arrange
                whenever(stockModel.getItems()).thenReturn(setOf(Material.STICK))

                // Act
                val response = model.getStackPrice(listOf(
                    itemStackOf(Material.STICK, amount = 3),
                    itemStackOf(Material.STICK, amount = 2)))

                // Assert
                assertEquals(20.0, response)
            }
        }
    }

    @Nested
    inner class DynamicPricingTest {
        private val stockModel = mock<StockManager>()

        private lateinit var model: DynamicPricingManager

        @BeforeEach
        fun setup() {
            whenever(stockModel.getItems()).thenReturn(setOf(Material.STICK))

            model = DynamicPricingManager(
                stockModel, mapOf(
                    Material.STICK to Price(4.0)
                ), 0.9, 0.125
            )
        }

        @Nested
        inner class GetPriceTest {
            @Test
            fun `call with item in stock`() {
                // Arrange
                whenever(stockModel.getStock(Material.STICK)).thenReturn(Stock(5.0, 5.0))

                // Act
                val response = model.getAmountPrice(ItemAmount(Material.STICK, 3.0))

                // Assert
                Assertions.assertEquals(6.0, response)
            }

            @Test
            fun `call with item in stock and price adjustment`() {
                // Arrange
                val priceAdjustment = PriceAdjustment(1.5, 0.5)
                whenever(stockModel.getStock(Material.STICK)).thenReturn(Stock(5.0, 5.0))

                // Act
                val response = model.getAmountPrice(ItemAmount(Material.STICK, 3.0), priceAdjustment)

                // Assert
                Assertions.assertEquals(12.0, response)
            }

            @Test
            fun `call with item not in stock`() {
                // Arrange
                whenever(stockModel.getStock(Material.STICK)).thenReturn(Stock(5.0, 5.0))

                // Act
                val response = model.getAmountPrice(ItemAmount(Material.OAK_LOG, 3.0))

                // Assert
                Assertions.assertNull(response)
            }

            @Test
            fun `call with multiple same items`() {
                // Arrange
                whenever(stockModel.getStock(Material.STICK)).thenReturn(Stock(5.0, 5.0))

                // Act
                val response = model.getStackPrice(listOf(
                    itemStackOf(Material.STICK, amount = 3),
                    itemStackOf(Material.STICK, amount = 2)))

                // Assert
                assertEquals(10.0, response)
            }
        }
    }

    @Nested
    inner class DerivedPricingTest {
        private val stockModel = mock<ReducingStockManager>()

        private lateinit var model: DerivedPricingManager

        @BeforeEach
        fun setup() {
            whenever(stockModel.getBaseItems()).thenReturn(setOf(Material.OAK_LOG))

            model = DerivedPricingManager(
                stockModel, mapOf(
                    Material.OAK_LOG to 4.0
                )
            )
        }

        @Nested
        inner class GetPriceTest {
            @Test
            fun `call with item in stock`() {
                // Arrange
                whenever(stockModel.getStock(Material.OAK_LOG)).thenReturn(Stock(5.0, 5.0))
                whenever(stockModel.reduce(any())).thenReturn(mapOf(Material.OAK_LOG to 2.0))

                // Act
                val response = model.getAmountPrice(ItemAmount(Material.STICK, 3.0))

                // Assert
                Assertions.assertEquals(8.0, response)
            }

            @Test
            fun `call with item in stock and price adjustment`() {
                // Arrange
                val priceAdjustment = PriceAdjustment(1.5, 0.5)
                whenever(stockModel.getStock(Material.OAK_LOG)).thenReturn(Stock(5.0, 5.0))
                whenever(stockModel.reduce(any())).thenReturn(mapOf(Material.OAK_LOG to 2.0))

                // Act
                val response = model.getAmountPrice(ItemAmount(Material.STICK, 3.0), priceAdjustment)

                // Assert
                Assertions.assertEquals(16.0, response)
            }

            @Test
            fun `call with item not in stock`() {
                // Arrange
                whenever(stockModel.getStock(Material.OAK_LOG)).thenReturn(Stock(5.0, 5.0))
                whenever(stockModel.reduce(any())).thenReturn(mapOf(Material.OAK_PLANKS to 3.0))

                // Act
                val response = model.getAmountPrice(ItemAmount(Material.OAK_PLANKS, 3.0))

                // Assert
                Assertions.assertNull(response)
            }

            @Test
            fun `call with multiple same items`() {
                // Arrange
                whenever(stockModel.getStock(Material.OAK_LOG)).thenReturn(Stock(5.0, 5.0))
                whenever(stockModel.reduce(any())).thenReturn(mapOf(Material.OAK_LOG to 2.0))

                // Act
                val response = model.getStackPrice(listOf(
                    itemStackOf(Material.STICK, amount = 3),
                    itemStackOf(Material.STICK, amount = 2)))

                // Assert
                assertEquals(8.0, response)
            }
        }
    }

    @Nested
    inner class DerivedDynamicPricingTest {
        private val stockModel = mock<ReducingStockManager>()

        private lateinit var model: DerivedDynamicPricingManager

        @BeforeEach
        fun setup() {
            whenever(stockModel.getBaseItems()).thenReturn(setOf(Material.OAK_LOG))

            model = DerivedDynamicPricingManager(
                stockModel, mapOf(
                    Material.OAK_LOG to Price(4.0)
                ), 0.9, 0.125
            )
        }

        @Nested
        inner class GetPriceTest {
            @Test
            fun `call with item in stock`() {
                // Arrange
                whenever(stockModel.getStock(Material.OAK_LOG)).thenReturn(Stock(5.0, 5.0))
                whenever(stockModel.reduce(any())).thenReturn(mapOf(Material.OAK_LOG to 4.0))

                // Act
                val response = model.getAmountPrice(ItemAmount(Material.STICK, 3.0))

                // Assert
                Assertions.assertEquals(8.0, response)
            }

            @Test
            fun `call with item in stock and price adjustment`() {
                // Arrange
                val priceAdjustment = PriceAdjustment(1.5, 0.5)
                whenever(stockModel.getStock(Material.OAK_LOG)).thenReturn(Stock(5.0, 5.0))
                whenever(stockModel.reduce(any())).thenReturn(mapOf(Material.OAK_LOG to 4.0))

                // Act
                val response = model.getAmountPrice(ItemAmount(Material.STICK, 3.0), priceAdjustment)

                // Assert
                Assertions.assertEquals(16.0, response)
            }

            @Test
            fun `call with item not in stock`() {
                // Arrange
                whenever(stockModel.getStock(Material.OAK_LOG)).thenReturn(Stock(5.0, 5.0))
                whenever(stockModel.reduce(any())).thenReturn(mapOf(Material.OAK_PLANKS to 3.0))

                // Act
                val response = model.getAmountPrice(ItemAmount(Material.OAK_PLANKS, 3.0))

                // Assert
                Assertions.assertNull(response)
            }

            @Test
            fun `call with multiple same items`() {
                // Arrange
                whenever(stockModel.getStock(Material.OAK_LOG)).thenReturn(Stock(5.0, 5.0))
                whenever(stockModel.reduce(any())).thenReturn(mapOf(Material.OAK_LOG to 4.0))

                // Act
                val response = model.getStackPrice(listOf(
                    itemStackOf(Material.STICK, amount = 3),
                    itemStackOf(Material.STICK, amount = 2)))

                // Assert
                assertEquals(8.0, response)
            }
        }
    }
}
