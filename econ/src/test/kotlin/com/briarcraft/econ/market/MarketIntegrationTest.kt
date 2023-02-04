package com.briarcraft.econ.market

import com.briarcraft.econ.currency.CurrencyExchange
import com.briarcraft.econ.currency.CurrencyServiceImpl
import com.briarcraft.econ.currency.DigitalCurrency
import com.briarcraft.econ.currency.PlayerWallet
import com.briarcraft.econ.api.price.Price
import com.briarcraft.econ.api.stock.Stock
import com.briarcraft.econ.api.stock.StockAmount
import com.briarcraft.econ.material.*
import com.briarcraft.econ.recipe.*
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import java.io.File
import java.text.DecimalFormat

class MarketIntegrationTest {
    @Nested
    inner class DynamicMarketIntegrationTest {
        private val plugin = mock<Plugin>()
        private val player = mock<Player>()

        private val recipeManager = CacheRecipeManager(File("src/test/resources/recipes.json")).also { it.loadRecipes() }
        private val items = ItemMaterials(InvalidItemMaterials(), UnavailableItemMaterials())
        private val excludedItems = ExcludedItemMaterials()
        private val recipeService = RecipeServiceImpl(plugin, listOf("minecraft", "manual"), recipeManager)
        private val materials = BaseItemMaterials(items, excludedItems, recipeService)
        private val currency = DigitalCurrency(
            "berry",
            "berries",
            "Ƀ",
            NamedTextColor.AQUA,
            DecimalFormat("#,##0"),
            NamedTextColor.DARK_AQUA
        )
        private val wallet = PlayerWallet(player)
        private val currencyService = CurrencyServiceImpl(plugin,
            currency, mutableMapOf(currency.name to currency), mutableMapOf(player to wallet),
            CurrencyExchange(mapOf())
        )
        private val reduceItems: Map<Material, Map<Material, Double>> = getReduceMappings(recipeService)
        private val baseItems = materials.types.associateWith { _ -> StockAmount(0.0, 100.0) to Price(100.0) }

        private lateinit var market: DynamicMarket

        @BeforeEach
        fun setup() {
            market = DynamicMarket("Dynamic", currencyService, currency, reduceItems, baseItems, 0.0, 0.125)
            wallet.remove(currency)
        }

        @Test
        fun `verify models`() {
            Assertions.assertEquals(baseItems.keys, market.stock.getBaseItems())
            Assertions.assertNull(market.stock.getStock(Material.FIREWORK_STAR))
//                assertNull(market.stock.getStock(Material.FIREWORK_ROCKET))
            Assertions.assertNull(market.stock.getStock(Material.VILLAGER_SPAWN_EGG))
        }

        @Test
        fun `call with no stock`() {
            // Arrange
            wallet.add(currency.createMoney(10_000.0))

            // Act
            val response = market.buy(ItemStack(Material.DIRT, 10), wallet)

            // Assert
            Assertions.assertFalse(response!!)
            Assertions.assertEquals(10_000.0, wallet.get(currency))
        }

        @Test
        fun `call with not enough stock`() {
            // Arrange
            wallet.add(currency.createMoney(10_000.0))
            market.stock.add(ItemStack(Material.DIRT, 9))

            // Act
            val response = market.buy(ItemStack(Material.DIRT, 10), wallet)

            // Assert
            Assertions.assertFalse(response!!)
            Assertions.assertEquals(Stock(9.0, 91.0), market.stock.getStock(Material.DIRT))
            Assertions.assertEquals(10_000.0, wallet.get(currency))
        }

        @Test
        fun `call with stock`() {
            // Arrange
            wallet.add(currency.createMoney(10_000.0))
            market.stock.add(ItemStack(Material.DIRT, 100))

            // Act
            val response = market.buy(ItemStack(Material.DIRT, 10), wallet)

            // Assert
            Assertions.assertTrue(response!!)
            Assertions.assertEquals(Stock(90.0, 10.0), market.stock.getStock(Material.DIRT))
            Assertions.assertEquals(9_875.0, wallet.get(currency))
        }
    }

    @Nested
    inner class DynamicMarketConfigIntegrationTest {
        private val plugin = mock<Plugin>()
        private val player = mock<Player>()

        private val recipeManager = CacheRecipeManager(File("src/test/resources/recipes.json")).also { it.loadRecipes() }

        private val recipeSetConfig = YamlConfiguration().also { it.load(File("src/main/resources/recipes.yml")) }
        private val materialSetConfig = YamlConfiguration().also { it.load(File("src/main/resources/materials.yml")) }

        private val recipeService = RecipeServiceImpl(plugin, listOf("minecraft", "manual"), recipeManager)
        private val materialService = MaterialServiceImpl(plugin, recipeService)

        private val materials = BaseItemMaterials(materialService.allItems, materialService.excludedItems, recipeService)
        private val currency = DigitalCurrency(
            "berry",
            "berries",
            "Ƀ",
            NamedTextColor.AQUA,
            DecimalFormat("#,##0"),
            NamedTextColor.DARK_AQUA
        )
        private val wallet = PlayerWallet(player)
        private val currencyService = CurrencyServiceImpl(plugin,
            currency, mutableMapOf(currency.name to currency), mutableMapOf(player to wallet),
            CurrencyExchange(mapOf())
        )
        private val reduceItems: Map<Material, Map<Material, Double>> = getReduceMappings(recipeService)
        private val baseItems = materials.types.associateWith { _ -> StockAmount(0.0, 100.0) to Price(100.0) }

        private lateinit var market: DynamicMarket

        @BeforeEach
        fun setup() {
            market = DynamicMarket("Dynamic", currencyService, currency, reduceItems, baseItems, 0.0, 0.125)
            wallet.remove(currency)
        }

        @Test
        fun `verify models`() {
            Assertions.assertEquals(baseItems.keys, market.stock.getBaseItems())
            Assertions.assertNull(market.stock.getStock(Material.FIREWORK_STAR))
//                assertNull(market.stock.getStock(Material.FIREWORK_ROCKET))
            Assertions.assertNull(market.stock.getStock(Material.VILLAGER_SPAWN_EGG))
        }

        @Test
        fun `call with no stock`() {
            // Arrange
            wallet.add(currency.createMoney(10_000.0))

            // Act
            val response = market.buy(ItemStack(Material.DIRT, 10), wallet)

            // Assert
            Assertions.assertFalse(response!!)
            Assertions.assertEquals(10_000.0, wallet.get(currency))
        }

        @Test
        fun `call with not enough stock`() {
            // Arrange
            wallet.add(currency.createMoney(10_000.0))
            market.stock.add(ItemStack(Material.DIRT, 9))

            // Act
            val response = market.buy(ItemStack(Material.DIRT, 10), wallet)

            // Assert
            Assertions.assertFalse(response!!)
            Assertions.assertEquals(Stock(9.0, 91.0), market.stock.getStock(Material.DIRT))
            Assertions.assertEquals(10_000.0, wallet.get(currency))
        }

        @Test
        fun `call with stock`() {
            // Arrange
            wallet.add(currency.createMoney(10_000.0))
            market.stock.add(ItemStack(Material.DIRT, 100))

            // Act
            val response = market.buy(ItemStack(Material.DIRT, 10), wallet)

            // Assert
            Assertions.assertTrue(response!!)
            Assertions.assertEquals(Stock(90.0, 10.0), market.stock.getStock(Material.DIRT))
            Assertions.assertEquals(9_875.0, wallet.get(currency))
        }
    }
}
