package com.briarcraft.econ.api.currency

import org.bukkit.OfflinePlayer
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority

interface CurrencyService {
    val plugin: Plugin
    val defaultCurrency: Currency
    val currencies: Map<String, Currency>
    val wallets: Map<OfflinePlayer, Wallet>
    val exchange: Exchange

    fun registerService() = plugin.server.servicesManager.let { servicesManager ->
        if (!servicesManager.isProvidedFor(CurrencyService::class.java)) {
            servicesManager.register(CurrencyService::class.java, this, plugin, ServicePriority.Normal)
            true
        } else false
    }

    fun getWallet(player: OfflinePlayer): Wallet
}
