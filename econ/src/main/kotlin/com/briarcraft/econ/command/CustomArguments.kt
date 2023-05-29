package com.briarcraft.econ.command

import com.briarcraft.econ.api.currency.CurrencyService
import com.briarcraft.econ.api.market.Market
import com.briarcraft.econ.api.market.MarketService
import com.briarcraft.econ.api.market.view.MarketView
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer

fun offlinePlayerArgument(nodeName: String = "player"): Argument<OfflinePlayer> =
    CustomArgument(StringArgument(nodeName)) { nameArg ->
        Bukkit.getOfflinePlayerIfCached(nameArg.input)
            ?: Bukkit.getPlayer(nameArg.input)
            ?: throw CustomArgument.CustomArgumentException(CustomArgument.MessageBuilder("That player does not exist"))
    }.replaceSuggestions(ArgumentSuggestions.strings {
        (Bukkit.getOnlinePlayers().map { it.name }.toSet() + Bukkit.getOfflinePlayers().map { it.name }.toSet())
            .filterNotNull()
            .toTypedArray()
    })

fun offlinePlayerNotSelfArgument(nodeName: String = "player"): Argument<OfflinePlayer> =
    CustomArgument(StringArgument(nodeName)) { nameArg ->
        Bukkit.getOfflinePlayerIfCached(nameArg.input)
            ?: Bukkit.getPlayer(nameArg.input)
            ?: throw CustomArgument.CustomArgumentException(CustomArgument.MessageBuilder("That player does not exist"))
    }.replaceSuggestions(ArgumentSuggestions.strings { info ->
        (Bukkit.getOnlinePlayers()
            .filterNot { it == info.sender }
            .map { it.name }
            .toSet() + Bukkit.getOfflinePlayers().map { it.name }.toSet())
            .filterNotNull()
            .toTypedArray()
    })

fun itemArgument(items: Set<Material>, nodeName: String = "item"): Argument<Material> =
    CustomArgument(StringArgument(nodeName)) { nameArg ->
        val material = Material.getMaterial(nameArg.input)
        if (material != null && items.contains(material)) material
        else throw CustomArgument.CustomArgumentException(CustomArgument.MessageBuilder("That item does not exist"))
    }.replaceSuggestions(ArgumentSuggestions.strings {
        items
            .map(Material::name)
            .toTypedArray()
    })

fun currencyArgument(currencyService: CurrencyService, nodeName: String = "currency"): Argument<com.briarcraft.econ.api.currency.Currency> =
    CustomArgument(StringArgument(nodeName)) { nameArg ->
        currencyService.currencies[nameArg.input]
            ?: throw CustomArgument.CustomArgumentException(CustomArgument.MessageBuilder("That currency does not exist"))
    }.replaceSuggestions(ArgumentSuggestions.strings {
        currencyService.currencies
            .map { it.key }
            .toTypedArray()
    })

fun marketArgument(marketService: MarketService, nodeName: String = "markets3"): Argument<Market> =
    CustomArgument(StringArgument(nodeName)) { nameArg ->
        marketService.markets[nameArg.input]
            ?: throw CustomArgument.CustomArgumentException(CustomArgument.MessageBuilder("That market does not exist"))
    }.replaceSuggestions(ArgumentSuggestions.strings {
        marketService.markets
            .map { it.key }
            .toTypedArray()
    })

fun marketViewArgument(marketService: MarketService, nodeName: String = "views"): Argument<MarketView> =
    CustomArgument(StringArgument(nodeName)) { nameArg ->
        marketService.views[nameArg.input]
            ?: throw CustomArgument.CustomArgumentException(CustomArgument.MessageBuilder("That market view does not exist"))
    }.replaceSuggestions(ArgumentSuggestions.strings {
        marketService.views
            .map { it.key }
            .toTypedArray()
    })

fun marketItemArgument(items: Set<Material>, nodeName: String = "marketitem"): Argument<Material> =
    CustomArgument(StringArgument(nodeName)) { nameArg ->
        val material = Material.getMaterial(nameArg.input)
        if (material != null && items.contains(material)) {
            material
        } else {
            throw CustomArgument.CustomArgumentException(CustomArgument.MessageBuilder("That item does not exist"))
        }
    }.replaceSuggestions(ArgumentSuggestions.strings { info ->
        // TODO
        val market = info.previousArgs.args().first { it is Market } as Market
        market.stock.getItems()
            .map(Material::name)
            .toTypedArray()
    })

fun marketItemArgument(nodeName: String = "marketitem"): Argument<Material> =
    CustomArgument(StringArgument(nodeName)) { nameArg ->
        val material = Material.getMaterial(nameArg.input)
        material ?: throw CustomArgument.CustomArgumentException(CustomArgument.MessageBuilder("That item does not exist"))
    }.replaceSuggestions(ArgumentSuggestions.strings { info ->
        // TODO
        val market = info.previousArgs.args().first { it is Market } as Market
        market.stock.getItems()
            .map(Material::name)
            .toTypedArray()
    })
