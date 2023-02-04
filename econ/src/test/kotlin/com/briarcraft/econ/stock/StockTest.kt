package com.briarcraft.econ.stock

import com.briarcraft.econ.api.item.ItemAmount
import com.briarcraft.econ.api.stock.*
import org.bukkit.Material
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StockTest {
    @Nested
    inner class InfiniteStockTest {
        private lateinit var model: InfiniteStockManager

        @BeforeEach
        fun setup() {
            model = InfiniteStockManager(setOf(Material.STICK, Material.OAK_LOG))
        }

        @Nested
        inner class GetItemsTest {
            @Test
            fun `call with stock`() {
                // Act
                val response = model.getItems()

                // Assert
                Assertions.assertEquals(2, response.size)
                Assertions.assertTrue(response.contains(Material.STICK))
                Assertions.assertTrue(response.contains(Material.OAK_LOG))
            }
        }

        @Nested
        inner class GetStockTest {
            @Test
            fun `call with matching stock`() {
                // Act
                val response = model.getStock(Material.STICK)

                // Assert
                Assertions.assertEquals(Stock(), response)
            }

            @Test
            fun `call with no matching stock`() {
                // Act
                val response = model.getStock(Material.DIRT)

                // Assert
                Assertions.assertNull(response)
            }
        }

        @Nested
        inner class AddTest {
            @Test
            fun `call with item in model`() {
                // Act
                val response = model.add(ItemAmount(Material.STICK, 1.0))

                // Assert
                Assertions.assertTrue(response == true)
            }

            @Test
            fun `call with item not in model`() {
                // Act
                val response = model.add(ItemAmount(Material.DIRT, 1.0))

                // Assert
                Assertions.assertNull(response)
            }
        }

        @Nested
        inner class RemoveTest {
            @Test
            fun `call with item in model`() {
                // Act
                val response = model.remove(ItemAmount(Material.STICK, 1.0))

                // Assert
                Assertions.assertTrue(response == true)
            }

            @Test
            fun `call with item not in model`() {
                // Act
                val response = model.remove(ItemAmount(Material.DIRT, 1.0))

                // Assert
                Assertions.assertNull(response)
            }
        }
    }

    @Nested
    inner class FiniteStockTest {
        private lateinit var model: FiniteStockManager

        @BeforeEach
        fun setup() {
            model = FiniteStockManager(
                mapOf(
                    Material.STICK to StockAmount(1.0, 3.0),
                    Material.OAK_LOG to StockAmount(3.0, 4.0)
                )
            )
        }

        @Nested
        inner class GetItemsTest {
            @Test
            fun `call with stock`() {
                // Act
                val response = model.getItems()

                // Assert
                Assertions.assertEquals(2, response.size)
                Assertions.assertTrue(response.contains(Material.STICK))
                Assertions.assertTrue(response.contains(Material.OAK_LOG))
            }
        }

        @Nested
        inner class GetStockTest {
            @Test
            fun `call with matching stock`() {
                // Act
                val response = model.getStock(Material.STICK)

                // Assert
                Assertions.assertEquals(Stock(1.0, 2.0), response)
            }

            @Test
            fun `call with no matching stock`() {
                // Act
                val response = model.getStock(Material.DIRT)

                // Assert
                Assertions.assertNull(response)
            }
        }

        @Nested
        inner class AddTest {
            @Test
            fun `call with item in model`() {
                // Act
                val response = model.add(ItemAmount(Material.STICK, 1.0))

                // Assert
                Assertions.assertTrue(response == true)
                Assertions.assertEquals(2.0, model.getStock(Material.STICK)?.removable)
            }

            @Test
            fun `call with item not in model`() {
                // Act
                val response = model.add(ItemAmount(Material.DIRT, 1.0))

                // Assert
                Assertions.assertNull(response)
                Assertions.assertNull(model.getStock(Material.DIRT))
            }

            @Test
            fun `call with not enough item in model`() {
                // Act
                val response = model.add(ItemAmount(Material.STICK, 10.0))

                // Assert
                Assertions.assertFalse(response == true)
                Assertions.assertEquals(1.0, model.getStock(Material.STICK)?.removable)
            }
        }

        @Nested
        inner class RemoveTest {
            @Test
            fun `call with item in model`() {
                // Act
                val response = model.remove(ItemAmount(Material.STICK, 1.0))

                // Assert
                Assertions.assertTrue(response == true)
                Assertions.assertEquals(0.0, model.getStock(Material.STICK)?.removable)
            }

            @Test
            fun `call with item not in model`() {
                // Act
                val response = model.remove(ItemAmount(Material.DIRT, 1.0))

                // Assert
                Assertions.assertNull(response)
                Assertions.assertNull(model.getStock(Material.DIRT))
            }

            @Test
            fun `call with not enough item in model`() {
                // Act
                val response = model.remove(ItemAmount(Material.STICK, 10.0))

                // Assert
                Assertions.assertFalse(response == true)
                Assertions.assertEquals(1.0, model.getStock(Material.STICK)?.removable)
            }
        }
    }

    @Nested
    inner class BaseItemReducingStockTest {
        private lateinit var stockManager: BaseItemReducingStockManager

        @BeforeEach
        fun setup() {
            stockManager = BaseItemReducingStockManager(
                mapOf(
                    Material.OAK_SIGN to mapOf(Material.OAK_PLANKS to 2.0, Material.STICK to 0.3333333333333333),
                    Material.STICK to mapOf(Material.OAK_PLANKS to 0.5),
                    Material.OAK_PLANKS to mapOf(Material.OAK_LOG to 0.25)
                ),
                mapOf(
                    Material.OAK_LOG to StockAmount(3.0, 4.0)
                )
            )
        }

        @Nested
        inner class GetItemsTest {
            @Test
            fun `call with stock`() {
                // Act
                val response = stockManager.getItems()

                // Assert
                Assertions.assertEquals(4, response.size)
                Assertions.assertTrue(response.contains(Material.OAK_LOG))
                Assertions.assertTrue(response.contains(Material.OAK_PLANKS))
                Assertions.assertTrue(response.contains(Material.STICK))
                Assertions.assertTrue(response.contains(Material.OAK_SIGN))
            }
        }

        @Nested
        inner class GetStockTest {
            @Test
            fun `call with base item in stock`() {
                // Act
                val response = stockManager.getStock(Material.OAK_LOG)

                // Assert
                Assertions.assertEquals(Stock(3.0, 1.0), response)
            }

            @Test
            fun `call with matching stock`() {
                // Act
                val response = stockManager.getStock(Material.STICK)

                // Assert
                Assertions.assertEquals(Stock(24.0, 8.0), response)
            }

            @Test
            fun `call with no matching stock`() {
                // Act
                val response = stockManager.getStock(Material.DIRT)

                // Assert
                Assertions.assertNull(response)
            }
        }

        @Nested
        inner class AddTest {
            @Test
            fun `call with item in model`() {
                // Act
                val response = stockManager.add(ItemAmount(Material.STICK, 1.0))

                // Assert
                Assertions.assertTrue(response == true)
                Assertions.assertEquals(25.0, stockManager.getStock(Material.STICK)?.removable)
                Assertions.assertEquals(3.125, stockManager.getStock(Material.OAK_LOG)?.removable)
            }

            @Test
            fun `call with base item in model`() {
                // Act
                val response = stockManager.add(ItemAmount(Material.OAK_LOG, 1.0))

                // Assert
                Assertions.assertTrue(response == true)
                Assertions.assertEquals(4.0, stockManager.getStock(Material.OAK_LOG)?.removable)
            }

            @Test
            fun `call with item not in model`() {
                // Act
                val response = stockManager.add(ItemAmount(Material.DIRT, 1.0))

                // Assert
                Assertions.assertNull(response)
                Assertions.assertNull(stockManager.getStock(Material.DIRT))
            }

            @Test
            fun `call with not enough item in model`() {
                // Act
                val response = stockManager.add(ItemAmount(Material.STICK, 25.0))

                // Assert
                Assertions.assertFalse(response == true)
                Assertions.assertEquals(24.0, stockManager.getStock(Material.STICK)?.removable)
                Assertions.assertEquals(3.0, stockManager.getStock(Material.OAK_LOG)?.removable)
            }
        }

        @Nested
        inner class RemoveTest {
            @Test
            fun `call with item in model`() {
                // Act
                val response = stockManager.remove(ItemAmount(Material.STICK, 1.0))

                // Assert
                Assertions.assertTrue(response == true)
            }

            @Test
            fun `call with item not in model`() {
                // Act
                val response = stockManager.remove(ItemAmount(Material.DIRT, 1.0))

                // Assert
                Assertions.assertNull(response)
            }

            @Test
            fun `call with not enough item in model`() {
                // Act
                val response = stockManager.remove(ItemAmount(Material.STICK, 25.0))

                // Assert
                Assertions.assertFalse(response == true)
            }
        }
    }
}