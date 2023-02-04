package com.briarcraft.econ.currency

import com.briarcraft.econ.api.currency.Currency
import com.briarcraft.econ.api.currency.CurrencyService
import com.briarcraft.econ.api.currency.Wallet
import com.briarcraft.econ.plugin
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.Configuration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.text.DecimalFormat
import java.util.*

fun loadCurrencyService(plugin: Plugin, config: Configuration): CurrencyService {
    val currencies: MutableMap<String, Currency> = config.getConfigurationSection("currencies")
        ?.let { currencies ->
            currencies.getKeys(false)
                .map { name ->
                    val namePlural = currencies.getString("$name.plural")!!
                    val symbol = currencies.getString("$name.display.symbol")!!
                    val symbolColor = currencies.getString("$name.display.symbol-color")
                        ?.let{ NamedTextColor.NAMES.value(it) }!!
                    val format = DecimalFormat(currencies.getString("$name.display.format"))
                    val numberColor = currencies.getString("$name.display.number-color")
                        ?.let{ NamedTextColor.NAMES.value(it) }!!

                    // TODO Support different types of currency
                    DigitalCurrency(name, namePlural, symbol, symbolColor, format, numberColor)
                }
                .associateBy { it.name }
                .toMutableMap()
        }!!

    val defaultCurrencyName = config.getString("currency.default")!!
    val defaultCurrency = currencies[defaultCurrencyName]!!

    val wallets: MutableMap<OfflinePlayer, Wallet> = config.getConfigurationSection("wallets")
        ?.let { wallets ->
            wallets.getKeys(false)
                .associate { playerId ->
                    val playerUuid = UUID.fromString(playerId)
                    val player = plugin.server.getOfflinePlayer(playerUuid)
                    val wallet = PlayerWallet(player)
                    wallets.getConfigurationSection(playerId)
                        ?.let { monies ->
                            monies.getKeys(false).map { currencyName ->
                                val currency = currencies[currencyName]
                                if (currency != null) {
                                    val amount = monies.getDouble(currencyName)
                                    wallet.add(currency.createMoney(amount), "Starting balance")
                                }
                            }
                        }
                    player to wallet
                }
                .toMutableMap()
        } ?: mutableMapOf()

    // TODO: Configure
    val currencyExchange = CurrencyExchange(mapOf())

    return CurrencyServiceImpl(plugin, defaultCurrency, currencies, wallets, currencyExchange)
}

fun saveCurrencyService(currencyService: CurrencyService, config: YamlConfiguration, configFile: File) {
    config.set("currency.default", currencyService.defaultCurrency.name)

    currencyService.currencies.forEach { (currencyName, currency) ->
        config.set("currencies.$currencyName.plural", currency.namePlural)
        config.set("currencies.$currencyName.display.symbol", currency.symbol)
        config.set("currencies.$currencyName.display.symbol-color", currency.symbolColor.toString())
        config.set("currencies.$currencyName.display.format", currency.format.toPattern())
        config.set("currencies.$currencyName.display.number-color", currency.numberColor.toString())
    }

    config.set("currency.default", currencyService.defaultCurrency.name)

    currencyService.wallets.forEach { (player, wallet) ->
        plugin.logger.fine("Saving wallet for player ${player.name}")
        wallet.monies.forEach { (currency, amount) ->
            config.set("wallets.${player.uniqueId}.${currency.name}", amount)
        }
    }

    // TODO Currency Exchange

    config.save(configFile)
}
