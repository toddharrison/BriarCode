package com.briarcraft.econ.market

import com.briarcraft.econ.api.currency.Currency
import com.briarcraft.econ.api.currency.CurrencyService
import com.briarcraft.econ.api.market.Market
import com.briarcraft.econ.price.StaticPricingManager
import com.briarcraft.econ.stock.FiniteStockManager
import com.briarcraft.econ.api.stock.StockAmount
import org.bukkit.Material

class StaticMarket(
    override val name: String,
    override val currencyService: CurrencyService,
    override val currency: Currency,
    items: Map<Material, Pair<StockAmount, Double>>
): Market {
    override val pricing = StaticPricingManager(
        FiniteStockManager(items.map { it.key to it.value.first }.toMap()),
        items.map { it.key to it.value.second }.toMap()
    )
}
