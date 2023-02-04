package com.briarcraft.econ.market

import com.briarcraft.econ.api.currency.Currency
import com.briarcraft.econ.api.currency.CurrencyService
import com.briarcraft.econ.api.market.ReducingMarket
import com.briarcraft.econ.price.DerivedDynamicPricingManager
import com.briarcraft.econ.api.price.Price
import com.briarcraft.econ.stock.BaseItemReducingStockManager
import com.briarcraft.econ.api.stock.StockAmount
import com.briarcraft.econ.stock.reduceUsingMap
import org.bukkit.Material

class DynamicMarket(
    override val name: String,
    override val currencyService: CurrencyService,
    override val currency: Currency,
    reduceItems: Map<Material, Map<Material, Double>>,
    baseItems: Map<Material, Pair<StockAmount, Price>>,
    defaultCurve: Double,
    defaultMinPriceMultiplier: Double
): ReducingMarket {
    override val pricing = DerivedDynamicPricingManager(
        BaseItemReducingStockManager(
            reduceItems
                .mapValues { (_, map) -> reduceUsingMap(reduceItems, map) }
                .filterValues { baseItems.keys.containsAll(it.keys) },
            baseItems.map { it.key to it.value.first }.toMap()
        ),
        baseItems.map { it.key to it.value.second }.toMap(),
        defaultCurve,
        defaultMinPriceMultiplier
    )
}
