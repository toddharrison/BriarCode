package com.briarcraft.econ.api.market

import com.briarcraft.econ.api.price.DerivedPricingManager
import com.briarcraft.econ.api.stock.ReducingStockManager

interface ReducingMarket: Market {
    override val pricing: DerivedPricingManager<ReducingStockManager>
    override val stock: ReducingStockManager
        get() = pricing.stock
}
