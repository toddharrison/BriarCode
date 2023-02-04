package com.briarcraft.econ.currency

import com.briarcraft.econ.api.currency.Currency
import com.briarcraft.econ.api.currency.CurrencyService
import com.briarcraft.econ.api.currency.Wallet
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.Plugin

open class CurrencyServiceImpl(
    override val plugin: Plugin,
    override val defaultCurrency: Currency,
    override val currencies: MutableMap<String, Currency>,
    override val wallets: MutableMap<OfflinePlayer, Wallet>,
    override val exchange: CurrencyExchange
): CurrencyService {
    override fun getWallet(player: OfflinePlayer) = wallets.getOrPut(player) { PlayerWallet(player) }
}
