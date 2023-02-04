package com.briarcraft.econ.api.price

import com.briarcraft.econ.api.item.ItemAmount
import com.briarcraft.econ.api.stock.StockManager
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import kotlin.math.max

interface PricingManager {
    val stock: StockManager
    fun getUnitPrice(type: Material, priceAdjustment: PriceAdjustment? = null): Double?
    fun getMaxPrice(type: Material, priceAdjustment: PriceAdjustment? = null): Double?
    fun getStackPrice(item: ItemStack, priceAdjustment: PriceAdjustment? = null): Double?
    fun getStackPrice(items: Iterable<ItemStack>, priceAdjustment: PriceAdjustment? = null): Double?
    fun getAmountPrice(item: ItemAmount, priceAdjustment: PriceAdjustment? = null): Double?
    fun getAmountPrice(items: Iterable<ItemAmount>, priceAdjustment: PriceAdjustment? = null): Double?
    fun getMapPrice(items: Map<Material, Double>, priceAdjustment: PriceAdjustment? = null): Double?

    fun adjustPrice(price: Double?, priceAdjustment: PriceAdjustment? = null): Double? {
        return if (price == null) {
            null
        } else if (priceAdjustment == null) {
            price
        } else {
            val adjustedPrice = price * priceAdjustment.priceMultiplier
            val fee = max(price * priceAdjustment.feePercentage, priceAdjustment.feeMinimum)
            priceAdjustment.modifier(adjustedPrice, fee)
        }
    }
}
