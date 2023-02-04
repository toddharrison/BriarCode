package com.briarcraft.econ.market

import com.briarcraft.econ.api.currency.Currency
import com.briarcraft.econ.api.currency.CurrencyService
import com.briarcraft.econ.api.market.Market
import com.briarcraft.econ.api.market.MarketService
import com.briarcraft.econ.api.market.view.MarketView
import com.briarcraft.econ.api.market.view.MarketViewGroup
import com.briarcraft.econ.api.price.PriceAdjustment
import com.briarcraft.econ.api.material.MaterialService
import com.briarcraft.econ.api.price.Price
import com.briarcraft.econ.api.recipe.RecipeService
import com.briarcraft.econ.api.stock.StockAmount
import com.briarcraft.econ.market.view.MarketBuyView
import com.briarcraft.econ.market.view.MarketSellView
import com.briarcraft.econ.recipe.getReduceMappings
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.configuration.Configuration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File

const val CONFIG_STOCK = "stock"
const val CONFIG_DEFAULT = "default"

const val MARKET_TYPE_FREE = "free"
const val MARKET_TYPE_STATIC = "static"
const val MARKET_TYPE_DYNAMIC = "dynamic"

const val VIEW_TYPE_BUY = "buy"
const val VIEW_TYPE_SELL = "sell"

fun loadMarketService(
    plugin: Plugin,
    currencyService: CurrencyService,
    recipeService: RecipeService,
    materialService: MaterialService,
    config: Configuration
): MarketService {
    val defaultMarketName = config.getString("$CONFIG_DEFAULT.market") ?: CONFIG_DEFAULT
    val marketNames = config.getStringList("markets")
    val viewNames = config.getStringList("views").toSet()
    val markets = configMarkets(plugin, currencyService, recipeService, materialService, marketNames)
    val groups = configGroups(plugin)
    val views = configViews(plugin, currencyService, markets, groups, viewNames)

    val defaultMarket: Market = markets.getOrPut(defaultMarketName) {
        FreeMarket(defaultMarketName, currencyService, currencyService.defaultCurrency, mapOf())
    }

    return MarketServiceImpl(plugin, defaultMarket, markets, views, groups)
}

fun saveMarketService(plugin: Plugin, marketService: MarketService) {
    marketService.markets.forEach { (name, market) ->
        val currencyName = market.currency.name
        when (market) {
            is FreeMarket -> {
                val typeName = MARKET_TYPE_FREE

                val config = YamlConfiguration()
                config.set("type", typeName)
                config.set("currency", currencyName)
                market.stock.getItems().forEach { material ->
                    config.set("$CONFIG_STOCK.$material.amount", market.stock.getStock(material)?.removable)
                    config.set("$CONFIG_STOCK.$material.max-amount", market.stock.getStock(material)?.getMaxAmount())
                }

                config.save(File(plugin.dataFolder, "markets${File.separator}$name.yml"))
            }
            is StaticMarket -> {
                val typeName = MARKET_TYPE_STATIC

                val config = YamlConfiguration()
                config.set("type", typeName)
                config.set("currency", currencyName)
                market.stock.getItems().forEach { material ->
                    config.set("$CONFIG_STOCK.$material.amount", market.stock.getStock(material)?.removable)
                    config.set("$CONFIG_STOCK.$material.max-amount", market.stock.getStock(material)?.getMaxAmount())
                    config.set("$CONFIG_STOCK.$material.max-price", market.pricing.getMaxPrice(material, null))
                }

                config.save(File(plugin.dataFolder, "markets${File.separator}$name.yml"))
            }
            is DynamicMarket -> {
                val typeName = MARKET_TYPE_DYNAMIC
                val defaultCurve = market.pricing.defaultCurve
                val defaultMinPriceModifier = market.pricing.defaultMinPriceMultiplier

                val config = YamlConfiguration()
                config.set("type", typeName)
                config.set("currency", currencyName)
                config.set("$CONFIG_DEFAULT.curve", defaultCurve)
                config.set("$CONFIG_DEFAULT.min-price-multiplier", defaultMinPriceModifier)
                market.stock.getBaseItems().forEach { material ->
                    config.set("$CONFIG_STOCK.$material.amount", market.stock.getStock(material)?.removable)
                    config.set("$CONFIG_STOCK.$material.max-amount", market.stock.getStock(material)?.getMaxAmount())
                    config.set("$CONFIG_STOCK.$material.max-price", market.pricing.getMaxPrice(material, null))
                }

                config.save(File(plugin.dataFolder, "markets${File.separator}$name.yml"))
            }
            else -> throw IllegalStateException("Market type '${market.javaClass}' not recognized")
        }
    }
}



private fun configMarkets(
    plugin: Plugin,
    currencyService: CurrencyService,
    recipeService: RecipeService,
    materialService: MaterialService,
    marketNames: List<String>
): MutableMap<String, Market> {
    return marketNames.associateWith { marketName ->
        val marketConfig = YamlConfiguration()
            .also { it.load(File(plugin.dataFolder, "markets${File.separator}$marketName.yml")) }
        val typeName = marketConfig.getString("type")
        val currencyName = marketConfig.getString("currency")!!
        val currency = currencyService.currencies[currencyName]!!

        when (typeName?.lowercase()) {
            MARKET_TYPE_FREE -> configFreeMarket(marketName, currencyService, currency, marketConfig)
            MARKET_TYPE_STATIC -> configStaticMarket(marketName, currencyService, currency, marketConfig)
            MARKET_TYPE_DYNAMIC -> configDynamicMarket(marketName, currencyService, currency, recipeService, materialService, marketConfig)
            else -> throw IllegalStateException("Market type '$typeName' not recognized")
        }
    }.toMutableMap()
}

private fun configFreeMarket(
    name: String,
    currencyService: CurrencyService,
    currency: Currency,
    config: Configuration
): FreeMarket {
    val items = config.getConfigurationSection(CONFIG_STOCK)
        ?.getKeys(false)
        ?.mapNotNull(Material::getMaterial)
        ?.associateWith { stockType ->
            val curAmount = config.getDouble("$CONFIG_STOCK.${stockType.name}.amount")
            val maxAmount = config.getDouble("$CONFIG_STOCK.${stockType.name}.max-amount", Double.NaN)

            StockAmount(curAmount, if (maxAmount.isNaN()) null else maxAmount)
        } ?: mapOf()

    return FreeMarket(name, currencyService, currency, items)
}

private fun configStaticMarket(
    name: String,
    currencyService: CurrencyService,
    currency: Currency,
    config: Configuration
): StaticMarket {
    val items = config.getConfigurationSection(CONFIG_STOCK)
        ?.getKeys(false)
        ?.mapNotNull(Material::getMaterial)
        ?.associateWith { stockType ->
            val curAmount = config.getDouble("$CONFIG_STOCK.${stockType.name}.amount")
            val maxAmount = config.getDouble("$CONFIG_STOCK.${stockType.name}.max-amount", Double.NaN)
            val maxPrice = config.getDouble("$CONFIG_STOCK.${stockType.name}.max-price")

            StockAmount(curAmount, if (maxAmount.isNaN()) null else maxAmount) to maxPrice
        } ?: mapOf()

    return StaticMarket(name, currencyService, currency, items)
}

private fun configDynamicMarket(
    name: String,
    currencyService: CurrencyService,
    currency: Currency,
    recipeService: RecipeService,
    materialService: MaterialService,
    config: Configuration
): DynamicMarket {
    val reduceItems = getReduceMappings(recipeService, materialService.materialSets)
    val defaultCurve = config.getDouble("$CONFIG_DEFAULT.curve", Double.NaN)
    val defaultMinPriceMultiplier = config.getDouble("$CONFIG_DEFAULT.min-price-multiplier", Double.NaN)

    val baseItems = config.getConfigurationSection(CONFIG_STOCK)
        ?.getKeys(false)
        ?.mapNotNull(Material::getMaterial)
        ?.associateWith { stockType ->
            val curAmount = config.getDouble("$CONFIG_STOCK.${stockType.name}.amount")
            val maxAmount = config.getDouble("$CONFIG_STOCK.${stockType.name}.max-amount", Double.NaN)
            val maxPrice = config.getDouble("$CONFIG_STOCK.${stockType.name}.max-price")
            val minPrice = config.getDouble("$CONFIG_STOCK.${stockType.name}.min-price", Double.NaN)
            val curve = config.getDouble("$CONFIG_STOCK.${stockType.name}.curve", Double.NaN)

            StockAmount(
                curAmount,
                if (maxAmount.isNaN()) null else maxAmount
            ) to Price(
                maxPrice,
                if(minPrice.isNaN()) null else minPrice,
                if (curve.isNaN()) null else curve
            )
        } ?: mapOf()

    // Validate
    check(!defaultCurve.isNaN()) { "'$CONFIG_DEFAULT.curve' is not set in market '$name' config" }
    check(!defaultMinPriceMultiplier.isNaN()) { "'$CONFIG_DEFAULT.min-price-multiplier' is not set in market '$name' config" }

    return DynamicMarket(name, currencyService, currency, reduceItems, baseItems, defaultCurve, defaultMinPriceMultiplier)
}

private fun configGroups(
    plugin: Plugin
): Map<String, MarketViewGroup> {
    return File(plugin.dataFolder, "markets${File.separator}views${File.separator}groups").walk()
        .filterNot { it.isDirectory }
        .filter { it.extension == "yml" || it.extension == "yaml" }
        .associate { file -> file.nameWithoutExtension to YamlConfiguration().also { it.load(file) } }
        .mapValues { (key, groupConfig) ->
            val title = groupConfig.getString("title")!!
            val iconName = groupConfig.getString("icon")!!
            val icon = Material.getMaterial(iconName)!!

            val items = groupConfig.getStringList("items").mapNotNull(Material::getMaterial).toSet()
            val subGroups = groupConfig.getStringList("sub-groups")

            MarketViewGroup(key, title, icon, items, subGroups)
        }
}

private fun configViews(
    plugin: Plugin,
    currencyService: CurrencyService,
    markets: Map<String, Market>,
    groups: Map<String, MarketViewGroup>,
    viewNames: Set<String>
): Map<String, MarketView> {
    return viewNames.associateWith { viewName ->
        val viewConfig = YamlConfiguration()
            .also { it.load(File(plugin.dataFolder, "markets${File.separator}views${File.separator}$viewName.yml")) }
        val title = viewConfig.getString("title")!!
        val marketName = viewConfig.getString("market")!!
        val market = markets[marketName]!!
        val typeName = viewConfig.getString("type")
        val priceMultiplier = viewConfig.getDouble("price-multiplier", Double.NaN).let { if (it.isNaN()) null else it }
        val feePercentage = viewConfig.getDouble("fee.percentage", Double.NaN).let { if (it.isNaN()) null else it }
        val feeMinimum = viewConfig.getDouble("fee.minimum", Double.NaN).let { if (it.isNaN()) null else it }
        val viewGroups = viewConfig.getStringList("groups").mapNotNull { groups[it] }

        when (typeName?.lowercase()) {
            VIEW_TYPE_BUY -> {
                val priceAdjustment = PriceAdjustment(priceMultiplier, feePercentage, feeMinimum)
                configBuyView(title, market, priceAdjustment, currencyService, groups, viewGroups)
            }
            VIEW_TYPE_SELL -> {
                val priceAdjustment = PriceAdjustment(priceMultiplier, feePercentage, feeMinimum) {
                        value, adjustment -> value - adjustment
                }
                configSellView(title, market, priceAdjustment, currencyService, groups, viewGroups)
            }
            else -> throw IllegalStateException("View type '$typeName' not recognized")
        }
    }
}

private fun configBuyView(
    title: String,
    market: Market,
    priceAdjustment: PriceAdjustment,
    currencyService: CurrencyService,
    groups: Map<String, MarketViewGroup>,
    viewGroups: List<MarketViewGroup>
): MarketBuyView {
    val titleDisplay = Component.text(title)

    val rootGroup = MarketViewGroup("root", "Root Group", Material.BARRIER, subGroups = viewGroups.map { it.key })

    // Validate
    check(!rootGroup.subGroups.isNullOrEmpty())

    return MarketBuyView(titleDisplay, market, currencyService, groups, rootGroup, priceAdjustment)
}

private fun configSellView(
    title: String,
    market: Market,
    priceAdjustment: PriceAdjustment,
    currencyService: CurrencyService,
    groups: Map<String, MarketViewGroup>,
    viewGroups: List<MarketViewGroup>
): MarketSellView {
    val titleDisplay = Component.text(title)

    val allowedItems = viewGroups.flatMap { it.getAllItems(groups) }.toSet()

    // Validate
    check(allowedItems.isNotEmpty())

    return MarketSellView(titleDisplay, market, currencyService, allowedItems, priceAdjustment)
}
