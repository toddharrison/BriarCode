package com.briarcraft.econ.market

import com.briarcraft.econ.api.currency.Currency
import com.briarcraft.econ.api.currency.CurrencyService
import com.briarcraft.econ.api.market.Market
import com.briarcraft.econ.price.FreePricingManager
import com.briarcraft.econ.stock.FiniteStockManager
import com.briarcraft.econ.api.stock.StockAmount
import org.bukkit.Material

class FreeMarket(
    override val name: String,
    override val currencyService: CurrencyService,
    override val currency: Currency,
    items: Map<Material, StockAmount>
): Market {
    override val pricing = FreePricingManager(
        FiniteStockManager(items)
    )
}
