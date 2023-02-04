package com.briarcraft.econ.market

import com.briarcraft.econ.api.currency.Currency
import com.briarcraft.econ.currency.CurrencyServiceImpl
import com.briarcraft.econ.api.currency.Money
import com.briarcraft.econ.api.currency.Wallet
import com.briarcraft.econ.api.price.Price
import com.briarcraft.econ.api.stock.StockAmount
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class MarketTest {
    @Nested
    inner class FreeMarketTest {
        private val currencyService = mock<CurrencyServiceImpl>()
        private val currency = mock<Currency>()
        private val wallet = mock<Wallet>()
        private val money = mock<Money>()

        private lateinit var market: FreeMarket

        @BeforeEach
        fun setup() {
            market = FreeMarket("Free", currencyService, currency, mapOf(Material.DIRT to StockAmount(50.0, 100.0)))
        }

        @Nested
        inner class BuyTest {
            @Test
            fun `call with item in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.remove(eq(money), any())).thenReturn(true)

                // Act
                val response = market.buy(ItemStack(Material.DIRT, 10), wallet)

                // Assert
                Assertions.assertTrue(response!!)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(60.0, market.stock.getStock(Material.DIRT)?.addable)

                verify(currency).createMoney(0.0)
                verify(wallet).remove(eq(money), any())
            }

            @Test
            fun `call with item in market not enough stock`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.remove(money)).thenReturn(true)

                // Act
                val response = market.buy(ItemStack(Material.DIRT, 51), wallet)

                // Assert
                Assertions.assertFalse(response!!)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.addable)

                verify(currency, never()).createMoney(any())
                verify(wallet, never()).remove(money)
            }

            @Test
            fun `call with item not in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.remove(money)).thenReturn(true)

                // Act
                val response = market.buy(ItemStack(Material.STICK, 10), wallet)

                // Assert
                Assertions.assertNull(response)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.addable)
                Assertions.assertNull(market.stock.getStock(Material.STICK))

                verify(currency, never()).createMoney(any())
                verify(wallet, never()).remove(money)
            }
        }

        @Nested
        inner class BuyUpToTest {
            @Test
            fun `call with item in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.get(currency)).thenReturn(1_000_000.0)
                whenever(wallet.remove(eq(money), any())).thenReturn(true)

                // Act
                val response = market.buyUpTo(ItemStack(Material.DIRT, 10), wallet)

                // Assert
                Assertions.assertEquals(0, response!!.amount)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(60.0, market.stock.getStock(Material.DIRT)?.addable)

                verify(currency).createMoney(0.0)
                verify(wallet).remove(eq(money), any())
            }

            @Test
            fun `call with item in market not enough stock`() {
                // Arrange
                whenever(currency.createMoney(0.0)).thenReturn(money)
                whenever(wallet.remove(eq(money), any())).thenReturn(true)

                // Act
                val response = market.buyUpTo(ItemStack(Material.DIRT, 51), wallet)

                // Assert
                Assertions.assertEquals(1, response!!.amount)
                Assertions.assertEquals(0.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(100.0, market.stock.getStock(Material.DIRT)?.addable)

                verify(currency).createMoney(0.0)
                verify(wallet).remove(eq(money), any())
            }

            @Test
            fun `call with item not in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.remove(money)).thenReturn(true)

                // Act
                val response = market.buyUpTo(ItemStack(Material.STICK, 10), wallet)

                // Assert
                Assertions.assertNull(response)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.addable)
                Assertions.assertNull(market.stock.getStock(Material.STICK))

                verify(currency, never()).createMoney(any())
                verify(wallet, never()).remove(money)
            }
        }

        @Nested
        inner class SellTest {
            @Test
            fun `call with item in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)

                // Act
                val response = market.sell(ItemStack(Material.DIRT, 10), wallet)

                // Assert
                Assertions.assertTrue(response!!)
                Assertions.assertEquals(60.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.DIRT)?.addable)

                verify(currency).createMoney(0.0)
                verify(wallet).add(eq(money), any())
            }

            @Test
            fun `call with item in market too much stock`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)

                // Act
                val response = market.sell(ItemStack(Material.DIRT, 60), wallet)

                // Assert
                Assertions.assertFalse(response!!)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.addable)

                verify(currency, never()).createMoney(any())
                verify(wallet, never()).add(money)
            }

            @Test
            fun `call with item not in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)

                // Act
                val response = market.sell(ItemStack(Material.STICK, 10), wallet)

                // Assert
                Assertions.assertNull(response)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.addable)
                Assertions.assertNull(market.stock.getStock(Material.STICK))

                verify(currency, never()).createMoney(any())
                verify(wallet, never()).add(money)
            }
        }

        @Nested
        inner class SellUpToTest {
            @Test
            fun `call with item in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)

                // Act
                val response = market.sellUpTo(ItemStack(Material.DIRT, 10), wallet)

                // Assert
                Assertions.assertEquals(0, response!!.amount)
                Assertions.assertEquals(60.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.DIRT)?.addable)

                verify(currency, never()).createMoney(any())
                verify(wallet, never()).add(any(), any())
            }

            @Test
            fun `call with item in market too much stock`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)

                // Act
                val response = market.sellUpTo(ItemStack(Material.DIRT, 60), wallet)

                // Assert
                Assertions.assertEquals(10, response!!.amount)
                Assertions.assertEquals(100.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(0.0, market.stock.getStock(Material.DIRT)?.addable)

                verify(currency, never()).createMoney(any())
                verify(wallet, never()).add(money)
            }

            @Test
            fun `call with item not in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)

                // Act
                val response = market.sellUpTo(ItemStack(Material.STICK, 10), wallet)

                // Assert
                Assertions.assertNull(response)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.addable)
                Assertions.assertNull(market.stock.getStock(Material.STICK))

                verify(currency, never()).createMoney(any())
                verify(wallet, never()).add(money)
            }
        }
    }

    @Nested
    inner class StaticMarketTest {
        private val currencyService = mock<CurrencyServiceImpl>()
        private val currency = mock<Currency>()
        private val wallet = mock<Wallet>()
        private val money = mock<Money>()

        private lateinit var market: StaticMarket

        @BeforeEach
        fun setup() {
            market = StaticMarket(
                "Static",
                currencyService,
                currency,
                mapOf(Material.DIRT to (StockAmount(50.0, 100.0) to 5.0))
            )
        }

        @Nested
        inner class BuyTest {
            @Test
            fun `call with item in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.remove(eq(money), any())).thenReturn(true)

                // Act
                val response = market.buy(ItemStack(Material.DIRT, 10), wallet)

                // Assert
                Assertions.assertTrue(response!!)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(60.0, market.stock.getStock(Material.DIRT)?.addable)

                verify(wallet).remove(eq(money), any())
            }

            @Test
            fun `call with item in market not enough stock`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.remove(money)).thenReturn(true)

                // Act
                val response = market.buy(ItemStack(Material.DIRT, 51), wallet)

                // Assert
                Assertions.assertFalse(response!!)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.addable)

                verify(wallet, never()).remove(money)
            }

            @Test
            fun `call with item not in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.remove(money)).thenReturn(true)

                // Act
                val response = market.buy(ItemStack(Material.STICK, 10), wallet)

                // Assert
                Assertions.assertNull(response)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.addable)
                Assertions.assertNull(market.stock.getStock(Material.STICK))

                verify(wallet, never()).remove(money)
            }
        }

        @Nested
        inner class BuyUpToTest {
            @Test
            fun `call with item in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.get(currency)).thenReturn(1_000_000.0)
                whenever(wallet.remove(eq(money), any())).thenReturn(true)

                // Act
                val response = market.buyUpTo(ItemStack(Material.DIRT, 10), wallet)

                // Assert
                Assertions.assertEquals(0, response!!.amount)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(60.0, market.stock.getStock(Material.DIRT)?.addable)

                verify(currency).createMoney(50.0)
                verify(wallet).remove(eq(money), any())
            }

            @Test
            fun `call with item in market not enough stock`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.get(currency)).thenReturn(1_000_000.0)
                whenever(wallet.remove(eq(money), any())).thenReturn(true)

                // Act
                val response = market.buyUpTo(ItemStack(Material.DIRT, 51), wallet)

                // Assert
                Assertions.assertEquals(1, response!!.amount)
                Assertions.assertEquals(0.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(100.0, market.stock.getStock(Material.DIRT)?.addable)

                verify(currency).createMoney(250.0)
                verify(wallet).remove(eq(money), any())
            }

            @Test
            fun `call with item not in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.remove(money)).thenReturn(true)

                // Act
                val response = market.buyUpTo(ItemStack(Material.STICK, 10), wallet)

                // Assert
                Assertions.assertNull(response)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.addable)
                Assertions.assertNull(market.stock.getStock(Material.STICK))

                verify(currency, never()).createMoney(any())
                verify(wallet, never()).remove(money)
            }
        }

        @Nested
        inner class SellTest {
            @Test
            fun `call with item in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)

                // Act
                val response = market.sell(ItemStack(Material.DIRT, 10), wallet)

                // Assert
                Assertions.assertTrue(response!!)
                Assertions.assertEquals(60.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.DIRT)?.addable)

                verify(wallet).add(eq(money), any())
            }

            @Test
            fun `call with item in market too much stock`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)

                // Act
                val response = market.sell(ItemStack(Material.DIRT, 60), wallet)

                // Assert
                Assertions.assertFalse(response!!)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.addable)

                verify(wallet, never()).add(money)
            }

            @Test
            fun `call with item not in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)

                // Act
                val response = market.sell(ItemStack(Material.STICK, 10), wallet)

                // Assert
                Assertions.assertNull(response)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.addable)
                Assertions.assertNull(market.stock.getStock(Material.STICK))

                verify(wallet, never()).add(money)
            }
        }

        @Nested
        inner class SellUpToTest {
            @Test
            fun `call with item in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.get(currency)).thenReturn(1_000_000.0)

                // Act
                val response = market.sellUpTo(ItemStack(Material.DIRT, 10), wallet)

                // Assert
                Assertions.assertEquals(0, response!!.amount)
                Assertions.assertEquals(60.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.DIRT)?.addable)

                verify(currency).createMoney(50.0)
                verify(wallet).add(any(), any())
            }

            @Test
            fun `call with item in market too much stock`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.get(currency)).thenReturn(1_000_000.0)

                // Act
                val response = market.sellUpTo(ItemStack(Material.DIRT, 60), wallet)

                // Assert
                Assertions.assertEquals(10, response!!.amount)
                Assertions.assertEquals(100.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(0.0, market.stock.getStock(Material.DIRT)?.addable)

                verify(currency).createMoney(250.0)
                verify(wallet).add(eq(money), any())
            }

            @Test
            fun `call with item not in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)

                // Act
                val response = market.sellUpTo(ItemStack(Material.STICK, 10), wallet)

                // Assert
                Assertions.assertNull(response)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.removable)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.DIRT)?.addable)
                Assertions.assertNull(market.stock.getStock(Material.STICK))

                verify(currency, never()).createMoney(any())
                verify(wallet, never()).add(money)
            }
        }
    }

    @Nested
    inner class DynamicMarketTest {
        private val currencyService = mock<CurrencyServiceImpl>()
        private val currency = mock<Currency>()
        private val wallet = mock<Wallet>()
        private val money = mock<Money>()

        private lateinit var market: DynamicMarket

        @BeforeEach
        fun setup() {
            market = DynamicMarket(
                "Dynamic",
                currencyService,
                currency,
                mapOf(
                    Material.STICK to mapOf(Material.OAK_PLANKS to 0.5),
                    Material.OAK_PLANKS to mapOf(Material.OAK_LOG to 0.25)
                ),
                mapOf(Material.OAK_LOG to (StockAmount(5.0, 10.0) to Price(10.0))),
                0.9, 0.125
            )
        }

        @Nested
        inner class BuyTest {
            @Test
            fun `call with base item in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.remove(eq(money), any())).thenReturn(true)

                // Act
                val response = market.buy(ItemStack(Material.OAK_LOG, 4), wallet)

                // Assert
                Assertions.assertTrue(response!!)
                Assertions.assertEquals(8.0, market.stock.getStock(Material.STICK)?.removable)
                Assertions.assertEquals(72.0, market.stock.getStock(Material.STICK)?.addable)
                Assertions.assertEquals(4.0, market.stock.getStock(Material.OAK_PLANKS)?.removable)
                Assertions.assertEquals(36.0, market.stock.getStock(Material.OAK_PLANKS)?.addable)
                Assertions.assertEquals(1.0, market.stock.getStock(Material.OAK_LOG)?.removable)
                Assertions.assertEquals(9.0, market.stock.getStock(Material.OAK_LOG)?.addable)

                verify(wallet).remove(eq(money), any())
            }

            @Test
            fun `call with item in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.remove(eq(money), any())).thenReturn(true)

                // Act
                val response = market.buy(ItemStack(Material.STICK, 10), wallet)

                // Assert
                Assertions.assertTrue(response!!)
                Assertions.assertEquals(30.0, market.stock.getStock(Material.STICK)?.removable)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.STICK)?.addable)
                Assertions.assertEquals(15.0, market.stock.getStock(Material.OAK_PLANKS)?.removable)
                Assertions.assertEquals(25.0, market.stock.getStock(Material.OAK_PLANKS)?.addable)
                Assertions.assertEquals(3.75, market.stock.getStock(Material.OAK_LOG)?.removable)
                Assertions.assertEquals(6.25, market.stock.getStock(Material.OAK_LOG)?.addable)

                verify(wallet).remove(eq(money), any())
            }

            @Test
            fun `call with item in market not enough stock`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.remove(money)).thenReturn(true)

                // Act
                val response = market.buy(ItemStack(Material.STICK, 51), wallet)

                // Assert
                Assertions.assertFalse(response!!)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.STICK)?.removable)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.STICK)?.addable)
                Assertions.assertEquals(20.0, market.stock.getStock(Material.OAK_PLANKS)?.removable)
                Assertions.assertEquals(20.0, market.stock.getStock(Material.OAK_PLANKS)?.addable)
                Assertions.assertEquals(5.0, market.stock.getStock(Material.OAK_LOG)?.removable)
                Assertions.assertEquals(5.0, market.stock.getStock(Material.OAK_LOG)?.addable)

                verify(wallet, never()).remove(money)
            }

            @Test
            fun `call with item not in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.remove(money)).thenReturn(true)

                // Act
                val response = market.buy(ItemStack(Material.DIRT, 10), wallet)

                // Assert
                Assertions.assertNull(response)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.STICK)?.removable)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.STICK)?.addable)
                Assertions.assertEquals(20.0, market.stock.getStock(Material.OAK_PLANKS)?.removable)
                Assertions.assertEquals(20.0, market.stock.getStock(Material.OAK_PLANKS)?.addable)
                Assertions.assertEquals(5.0, market.stock.getStock(Material.OAK_LOG)?.removable)
                Assertions.assertEquals(5.0, market.stock.getStock(Material.OAK_LOG)?.addable)
                Assertions.assertNull(market.stock.getStock(Material.DIRT))

                verify(wallet, never()).remove(money)
            }
        }

        @Nested
        inner class BuyUpToTest {
            @Test
            fun `call with base item in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.get(currency)).thenReturn(1_000_000.0)
                whenever(wallet.remove(eq(money), any())).thenReturn(true)

                // Act
                val response = market.buyUpTo(ItemStack(Material.OAK_LOG, 6), wallet)

                // Assert
                Assertions.assertEquals(1, response!!.amount)
                Assertions.assertEquals(0.0, market.stock.getStock(Material.STICK)?.removable)
                Assertions.assertEquals(80.0, market.stock.getStock(Material.STICK)?.addable)
                Assertions.assertEquals(0.0, market.stock.getStock(Material.OAK_PLANKS)?.removable)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.OAK_PLANKS)?.addable)
                Assertions.assertEquals(0.0, market.stock.getStock(Material.OAK_LOG)?.removable)
                Assertions.assertEquals(10.0, market.stock.getStock(Material.OAK_LOG)?.addable)

                verify(wallet).remove(eq(money), any())
            }

            @Test
            fun `call with item in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.get(currency)).thenReturn(1_000_000.0)
                whenever(wallet.remove(eq(money), any())).thenReturn(true)

                // Act
                val response = market.buyUpTo(ItemStack(Material.STICK, 10), wallet)

                // Assert
                Assertions.assertEquals(0, response!!.amount)
                Assertions.assertEquals(30.0, market.stock.getStock(Material.STICK)?.removable)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.STICK)?.addable)

                verify(currency).createMoney(6.25)
                verify(wallet).remove(eq(money), any())
            }

            @Test
            fun `call with item in market not enough stock`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.get(currency)).thenReturn(1_000_000.0)
                whenever(wallet.remove(eq(money), any())).thenReturn(true)

                // Act
                val response = market.buyUpTo(ItemStack(Material.STICK, 51), wallet)

                // Assert
                Assertions.assertEquals(11, response!!.amount)
                Assertions.assertEquals(0.0, market.stock.getStock(Material.STICK)?.removable)
                Assertions.assertEquals(80.0, market.stock.getStock(Material.STICK)?.addable)

                verify(currency).createMoney(25.0)
                verify(wallet).remove(eq(money), any())
            }

            @Test
            fun `call with item not in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.remove(money)).thenReturn(true)

                // Act
                val response = market.buyUpTo(ItemStack(Material.DIRT, 10), wallet)

                // Assert
                Assertions.assertNull(response)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.STICK)?.removable)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.STICK)?.addable)
                Assertions.assertNull(market.stock.getStock(Material.DIRT))

                verify(currency, never()).createMoney(any())
                verify(wallet, never()).remove(money)
            }
        }

        @Nested
        inner class SellTest {
            @Test
            fun `call with base item in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)

                // Act
                val response = market.sell(ItemStack(Material.OAK_LOG, 5), wallet)

                // Assert
                Assertions.assertTrue(response!!)
                Assertions.assertEquals(80.0, market.stock.getStock(Material.STICK)?.removable)
                Assertions.assertEquals(0.0, market.stock.getStock(Material.STICK)?.addable)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.OAK_PLANKS)?.removable)
                Assertions.assertEquals(0.0, market.stock.getStock(Material.OAK_PLANKS)?.addable)
                Assertions.assertEquals(10.0, market.stock.getStock(Material.OAK_LOG)?.removable)
                Assertions.assertEquals(0.0, market.stock.getStock(Material.OAK_LOG)?.addable)

                verify(wallet).add(eq(money), any())
            }

            @Test
            fun `call with item in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)

                // Act
                val response = market.sell(ItemStack(Material.STICK, 10), wallet)

                // Assert
                Assertions.assertTrue(response!!)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.STICK)?.removable)
                Assertions.assertEquals(30.0, market.stock.getStock(Material.STICK)?.addable)
                Assertions.assertEquals(25.0, market.stock.getStock(Material.OAK_PLANKS)?.removable)
                Assertions.assertEquals(15.0, market.stock.getStock(Material.OAK_PLANKS)?.addable)
                Assertions.assertEquals(6.25, market.stock.getStock(Material.OAK_LOG)?.removable)
                Assertions.assertEquals(3.75, market.stock.getStock(Material.OAK_LOG)?.addable)

                verify(wallet).add(eq(money), any())
            }

            @Test
            fun `call with item in market too much stock`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)

                // Act
                val response = market.sell(ItemStack(Material.STICK, 60), wallet)

                // Assert
                Assertions.assertFalse(response!!)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.STICK)?.removable)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.STICK)?.addable)
                Assertions.assertEquals(20.0, market.stock.getStock(Material.OAK_PLANKS)?.removable)
                Assertions.assertEquals(20.0, market.stock.getStock(Material.OAK_PLANKS)?.addable)
                Assertions.assertEquals(5.0, market.stock.getStock(Material.OAK_LOG)?.removable)
                Assertions.assertEquals(5.0, market.stock.getStock(Material.OAK_LOG)?.addable)

                verify(wallet, never()).add(money)
            }

            @Test
            fun `call with item not in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)

                // Act
                val response = market.sell(ItemStack(Material.DIRT, 10), wallet)

                // Assert
                Assertions.assertNull(response)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.STICK)?.removable)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.STICK)?.addable)
                Assertions.assertEquals(20.0, market.stock.getStock(Material.OAK_PLANKS)?.removable)
                Assertions.assertEquals(20.0, market.stock.getStock(Material.OAK_PLANKS)?.addable)
                Assertions.assertEquals(5.0, market.stock.getStock(Material.OAK_LOG)?.removable)
                Assertions.assertEquals(5.0, market.stock.getStock(Material.OAK_LOG)?.addable)
                Assertions.assertNull(market.stock.getStock(Material.DIRT))

                verify(wallet, never()).add(money)
            }
        }

        @Nested
        inner class SellUpToTest {
            @Test
            fun `call with base item in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)

                // Act
                val response = market.sellUpTo(ItemStack(Material.OAK_LOG, 6), wallet)

                // Assert
                Assertions.assertEquals(1, response!!.amount)
                Assertions.assertEquals(80.0, market.stock.getStock(Material.STICK)?.removable)
                Assertions.assertEquals(0.0, market.stock.getStock(Material.STICK)?.addable)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.OAK_PLANKS)?.removable)
                Assertions.assertEquals(0.0, market.stock.getStock(Material.OAK_PLANKS)?.addable)
                Assertions.assertEquals(10.0, market.stock.getStock(Material.OAK_LOG)?.removable)
                Assertions.assertEquals(0.0, market.stock.getStock(Material.OAK_LOG)?.addable)

                verify(wallet).add(eq(money), any())
            }

            @Test
            fun `call with item in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.get(currency)).thenReturn(1_000_000.0)

                // Act
                val response = market.sellUpTo(ItemStack(Material.STICK, 10), wallet)

                // Assert
                Assertions.assertEquals(0, response!!.amount)
                Assertions.assertEquals(50.0, market.stock.getStock(Material.STICK)?.removable)
                Assertions.assertEquals(30.0, market.stock.getStock(Material.STICK)?.addable)

                verify(currency).createMoney(6.25)
                verify(wallet).add(any(), any())
            }

            @Test
            fun `call with item in market too much stock`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)
                whenever(wallet.get(currency)).thenReturn(1_000_000.0)

                // Act
                val response = market.sellUpTo(ItemStack(Material.STICK, 60), wallet)

                // Assert
                Assertions.assertEquals(20, response!!.amount)
                Assertions.assertEquals(80.0, market.stock.getStock(Material.STICK)?.removable)
                Assertions.assertEquals(0.0, market.stock.getStock(Material.STICK)?.addable)

                verify(currency).createMoney(25.0)
                verify(wallet).add(eq(money), any())
            }

            @Test
            fun `call with item not in market`() {
                // Arrange
                whenever(currency.createMoney(any())).thenReturn(money)

                // Act
                val response = market.sellUpTo(ItemStack(Material.DIRT, 10), wallet)

                // Assert
                Assertions.assertNull(response)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.STICK)?.removable)
                Assertions.assertEquals(40.0, market.stock.getStock(Material.STICK)?.addable)
                Assertions.assertNull(market.stock.getStock(Material.DIRT))

                verify(currency, never()).createMoney(any())
                verify(wallet, never()).add(money)
            }
        }
    }
}