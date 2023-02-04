package com.briarcraft.econ.api.price

import com.briarcraft.econ.api.stock.StockManager
import org.bukkit.Material
import kotlin.math.abs
import kotlin.math.max

interface DerivedPricingManager<T: StockManager>: PricingManager {
    override val stock: T

//    val basePrices: Map<Material, Price>
    val defaultCurve: Double
    val defaultMinPriceMultiplier: Double



    fun getAdjustedPrice(type: Material, price: Price): Double? {
        val curve = price.curve ?: defaultCurve
        val stock = stock.getStock(type)
        return if (stock == null) null else {
            val currentAmount = stock.removable
            val maxAmount = stock.getMaxAmount()
            if (currentAmount == null || maxAmount == null) {
                price.maxPrice
            } else {
                val iqr = (2.0 * currentAmount / maxAmount) - 1.0
                val midPrice = price.maxPrice / 2.0
                val adjustedPrice = midPrice - midPrice * ((iqr - curve * iqr) / (curve - 2 * curve * abs(iqr) + 1.0))
                val minPrice = price.minPrice ?: (price.maxPrice * defaultMinPriceMultiplier)
                max(minPrice, adjustedPrice)
            }
        }
    }
}
