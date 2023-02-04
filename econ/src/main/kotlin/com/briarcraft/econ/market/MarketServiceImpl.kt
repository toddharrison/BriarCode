package com.briarcraft.econ.market

import com.briarcraft.econ.api.market.Market
import com.briarcraft.econ.api.market.MarketService
import com.briarcraft.econ.api.market.view.MarketViewGroup
import com.briarcraft.econ.api.market.view.MarketView
import org.bukkit.plugin.Plugin

class MarketServiceImpl(
    override val plugin: Plugin,
    override val defaultMarket: Market,
    override val markets: Map<String, Market>,
    override val views: Map<String, MarketView>,
    override val groups: Map<String, MarketViewGroup>
): MarketService {
}
