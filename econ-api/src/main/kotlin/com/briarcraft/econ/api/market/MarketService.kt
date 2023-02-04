package com.briarcraft.econ.api.market

import com.briarcraft.econ.api.market.view.MarketViewGroup
import com.briarcraft.econ.api.market.view.MarketView
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority

interface MarketService {
    val plugin: Plugin
    val defaultMarket: Market
    val markets: Map<String, Market>
    val views: Map<String, MarketView>
    val groups: Map<String, MarketViewGroup>

    fun registerService() = plugin.server.servicesManager.let { servicesManager ->
        if (!servicesManager.isProvidedFor(MarketService::class.java)) {
            servicesManager.register(MarketService::class.java, this, plugin, ServicePriority.Normal)
            true
        } else false
    }
}
