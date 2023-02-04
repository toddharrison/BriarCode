package com.briarcraft.econ.api.currency

import org.bukkit.OfflinePlayer

interface Wallet {
    val owner: OfflinePlayer

    val monies: Map<Currency, Double>

    fun get(currency: Currency): Double?
    fun add(money: Money, description: String? = null): Boolean
    fun remove(currency: Currency, description: String? = null): Double?
    fun remove(money: Money, description: String? = null): Boolean
    fun removeUpTo(money: Money, description: String? = null): Money?

    fun history(): List<Transaction>
}
