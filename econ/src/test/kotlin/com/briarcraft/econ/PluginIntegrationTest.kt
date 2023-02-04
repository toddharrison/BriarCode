package com.briarcraft.econ

import com.briarcraft.econ.api.currency.Currency
import com.briarcraft.econ.api.currency.CurrencyService
import com.briarcraft.econ.api.currency.Wallet
import com.briarcraft.econ.api.market.MarketService
import com.briarcraft.econ.api.material.MaterialService
import com.briarcraft.econ.api.recipe.RecipeManager
import com.briarcraft.econ.api.recipe.RecipeService
import com.briarcraft.econ.currency.loadCurrencyService
import com.briarcraft.econ.market.loadMarketService
import com.briarcraft.econ.material.MATERIALS_EXCLUDED
import com.briarcraft.econ.material.MATERIALS_INVALID
import com.briarcraft.econ.material.MATERIALS_UNAVAILABLE
import com.briarcraft.econ.material.loadMaterialService
import com.briarcraft.econ.recipe.*
import net.kyori.adventure.text.TextComponent
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.plugin.Plugin
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.File
import kotlin.test.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class PluginIntegrationTest {
    @Nested
    inner class ServiceTest {
        private val plugin = mock<Plugin>()

        private val currencyService: CurrencyService
        private val recipeManager: RecipeManager
        private val recipeService: RecipeService
        private val materialService: MaterialService
        private val marketService: MarketService

        private val defaultCurrency: Currency

        init {
            whenever(plugin.dataFolder).thenReturn(File("src/main/resources"))

            val currencyConfig = YamlConfiguration().also { it.load(File("src/main/resources/currencies.yml")) }
            currencyService = loadCurrencyService(plugin, currencyConfig)
            defaultCurrency = currencyService.defaultCurrency

            recipeManager = CacheRecipeManager(File("src/test/resources/recipes.json")).also { it.loadRecipes() }
            val recipeConfig = YamlConfiguration().also { it.load("src/main/resources/recipes.yml") }
            recipeService = loadRecipeService(plugin, recipeConfig, recipeManager)

            val materialConfig = YamlConfiguration().also { it.load("src/main/resources/materials.yml") }
            materialService = loadMaterialService(plugin, recipeService, materialConfig)

            val marketConfig = YamlConfiguration().also { it.load("src/main/resources/markets.yml") }
            marketService = loadMarketService(plugin, currencyService, recipeService, materialService, marketConfig)
        }

        @Nested
        inner class CurrencyServiceTest {
            private val player = mock<Player>()

            @Order(1)
            @Test
            fun `verify wallet creation`() {
                assertTrue(currencyService.wallets.isEmpty())
                assertNotNull(currencyService.getWallet(player))
                assertEquals(1, currencyService.wallets.size)
            }

            @Test
            fun `verify currencies`() {
                assertEquals(2, currencyService.currencies.size)
                assertEquals("berry", currencyService.currencies["berry"]?.name)
                assertEquals("dollar", currencyService.currencies["dollar"]?.name)
            }

            @Test
            fun `verify default currency`() {
                assertEquals("berry", currencyService.defaultCurrency.name)
            }

            @Test
            fun `verify wallet money`() {
                val wallet = currencyService.getWallet(player)
                assertNull(wallet.monies[defaultCurrency])
                wallet.add(defaultCurrency.createMoney(10_000.0))
                assertEquals(10_000.0, wallet.monies[defaultCurrency])
                assertTrue(wallet.remove(defaultCurrency.createMoney(100.0)))
                assertFalse(wallet.remove(defaultCurrency.createMoney(10_000.0)))
                assertEquals(9_900.0, wallet.monies[defaultCurrency])
                assertEquals(9_900.0, wallet.remove(defaultCurrency))
                assertNull(wallet.monies[defaultCurrency])
            }

            @Test
            fun `verify exchange`() {
                // TODO
                assertNotNull(currencyService.exchange)
            }
        }

        @Nested
        inner class RecipeManagerTest {
            @Test
            fun `verify available recipes`() {
                assertEquals(1089, recipeManager.getRecipes().count())
            }
        }

        @Nested
        inner class RecipeServiceTest {
            @Test
            fun `verify removed recipe set`() {
                assertEquals(31, recipeService.removedRecipes.recipes.size)
            }

            @Test
            fun `verify manual recipe set`() {
                assertEquals(78, recipeService.manualRecipes.recipes.size)
            }

            @Test
            fun `verify recipe sets`() {
                assertEquals(2, recipeService.recipeSets.size)
                assertEquals(78, recipeService.recipeSets[RECIPES_MANUAL]?.recipes?.size)
                assertEquals(31, recipeService.recipeSets[RECIPES_REMOVED]?.recipes?.size)
            }
        }

        @Nested
        inner class MaterialServiceTest {
            @Test
            fun `verify all items set`() {
                assertEquals(1155, materialService.allItems.types.size)
            }

            @Test
            fun `verify material sets`() {
                assertEquals(5, materialService.materialSets.size)
                assertEquals(79, materialService.materialSets[MATERIALS_EXCLUDED]?.types?.size)
                assertEquals(28, materialService.materialSets[MATERIALS_UNAVAILABLE]?.types?.size)
                assertEquals(64, materialService.materialSets[MATERIALS_INVALID]?.types?.size)
                assertEquals(3, materialService.materialSets[REDUCE_COBBLE]?.types?.size)
                assertEquals(3, materialService.materialSets[REDUCE_QUARTZ]?.types?.size)
            }

            @Test
            fun `verify excluded item set`() {
                assertEquals(79, materialService.excludedItems.types.size)
            }

            @Test
            fun `verify base item set`() {
                assertEquals(283, materialService.baseItems.types.size)
            }

            @Test
            fun `verify reduce items`() {
                assertEquals(794, materialService.reduceItems.keys.size)
                assertEquals(346, materialService.reduceItems.values.flatMap { it.keys }.toSet().size)
            }

            @Test
            fun `verify completeness of material sets`() {
                val allTypes = materialService.allItems.types
                val reduceTypes = materialService.reduceItems.keys + materialService.reduceItems.flatMap { it.value.keys }.toSet()
                val baseTypes = materialService.baseItems.types
                val excludedTypes = materialService.excludedItems.types

                assertTrue((allTypes - (reduceTypes + baseTypes + excludedTypes)).isEmpty())
                assertTrue(((reduceTypes + baseTypes + excludedTypes) - allTypes).isEmpty())
            }
        }

        @Nested
        inner class MarketServiceTest {
            @Test
            fun `verify markets`() {
                assertEquals(1, marketService.markets.size)
                assertEquals("main", marketService.markets["main"]?.name)
            }

            @Test
            fun `verify default market`() {
                assertEquals("main", marketService.defaultMarket.name)
            }

            @Test
            fun `verify groups`() {
                assertEquals(67, marketService.groups.size)
                assertEquals(20, marketService.groups["blocks"]?.subGroups?.size)
                assertEquals(23, marketService.groups["stone"]?.items?.size)
                assertEquals(36, marketService.groups["tools"]?.items?.size)
            }

            @Test
            fun `verify views`() {
                assertEquals(2, marketService.views.size)
                assertEquals("Buy Market", (marketService.views["main-buy"]?.title as TextComponent).content())
                assertEquals("Sell Market", (marketService.views["main-sell"]?.title as TextComponent).content())
            }

            @Nested
            inner class MarketTest {
                private val player = mock<Player>()
                private val playerInventory = mock<PlayerInventory>()

                private val market = marketService.defaultMarket

                @Test
                fun `verify name`() {
                    assertEquals("main", market.name)
                }

                @Test
                fun `verify currency`() {
                    assertEquals("berry", market.currency.name)
                }

                @Test
                fun `verify stock`() {
                    assertEquals(18, market.stock.getItems().size)
                    assertEquals(2062.0, market.stock.getItems().map { market.stock.getStock(it) }.mapNotNull { it?.removable }.sum())
                    assertEquals(2062.0, materialService.allItems.types.map { market.stock.getStock(it) }.mapNotNull { it?.removable }.sum())

                    assertEquals(99900.0, market.stock.getStock(Material.COBBLESTONE)?.addable)
                    assertEquals(100.0, market.stock.getStock(Material.COBBLESTONE)?.removable)

                    assertEquals(200.0, market.stock.getStock(Material.COBBLESTONE_SLAB)?.removable)
                    assertEquals(100.0, market.stock.getStock(Material.STONE)?.removable)

                    assertNull(market.stock.getStock(Material.STICK))
                    assertNull(market.stock.getStock(Material.LEVER))
                }

                @Test
                fun `verify pricing`() {
                    assertEquals(96.37546468401486, market.pricing.getUnitPrice(Material.COBBLESTONE))

                    assertEquals(48.18773234200743, market.pricing.getUnitPrice(Material.COBBLESTONE_SLAB))
                    assertEquals(96.37546468401486, market.pricing.getUnitPrice(Material.STONE))

                    assertNull(market.pricing.getUnitPrice(Material.STICK))
                }

                @Nested
                inner class TransactionTest {
                    private lateinit var currency: Currency
                    private lateinit var wallet: Wallet

                    @BeforeEach
                    fun setup() {
                        whenever(player.inventory).thenReturn(playerInventory)
                        whenever(playerInventory.contents).thenReturn(arrayOfNulls(36))
                        whenever(playerInventory.size).thenReturn(36)

                        currency = currencyService.defaultCurrency
                        wallet = currencyService.getWallet(player)

                        wallet.add(currency.createMoney(10_000.0))
                    }

                    @AfterEach
                    fun teardown() {
                        wallet.remove(currency)
                    }

                    @Test
                    fun `verify buy base item`() {
                        assertEquals(100.0, market.stock.getStock(Material.COBBLESTONE)?.removable)

                        assertTrue(market.buy(player, ItemStack(Material.COBBLESTONE, 16)) == true)
                        assertEquals(8457.0, wallet.get(currency))
                        assertEquals(84.0, market.stock.getStock(Material.COBBLESTONE)?.removable)

                        market.stock.add(ItemStack(Material.COBBLESTONE, 16))
                    }

                    @Test
                    fun `verify buy derived item`() {
                        assertEquals(100.0, market.stock.getStock(Material.COBBLESTONE)?.removable)

                        assertTrue(market.buy(player, ItemStack(Material.STONE, 16)) == true)
                        assertEquals(8457.0, wallet.get(currency))
                        assertEquals(84.0, market.stock.getStock(Material.COBBLESTONE)?.removable)

                        market.stock.add(ItemStack(Material.STONE, 16))
                    }

                    @Test
                    fun `verify buy unavailable item`() {
                        assertFalse(market.buy(player, ItemStack(Material.STICK, 16)) == true)
                        assertEquals(100.0, market.stock.getStock(Material.COBBLESTONE)?.removable)
                    }
                }
            }
        }
    }
}
