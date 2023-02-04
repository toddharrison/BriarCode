package com.briarcraft.econ.price

import com.briarcraft.econ.api.item.ItemAmount
import com.briarcraft.econ.api.price.PriceAdjustment
import com.briarcraft.econ.api.price.PricingManager
import com.briarcraft.econ.api.stock.StockManager
import com.briarcraft.kotlin.util.sumOfNullable
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class StaticPricingManager(override val stock: StockManager, prices: Map<Material, Double>): PricingManager {
    private val prices: Map<Material, Double> = HashMap(prices)

    init {
        require(stock.getItems().containsAll(prices.keys))
    }

    override fun getUnitPrice(type: Material, priceAdjustment: PriceAdjustment?) =
        adjustPrice(prices[type], priceAdjustment)
    override fun getMaxPrice(type: Material, priceAdjustment: PriceAdjustment?) =
        adjustPrice(prices[type], priceAdjustment)
    override fun getStackPrice(item: ItemStack, priceAdjustment: PriceAdjustment?) =
        getMapPrice(mapOf(item.type to item.amount.toDouble()), priceAdjustment)
    override fun getStackPrice(items: Iterable<ItemStack>, priceAdjustment: PriceAdjustment?) =
        getMapPrice(items.groupBy { it.type }.mapValues { (_, items) -> items.sumOf { it.amount }.toDouble() }, priceAdjustment)
    override fun getAmountPrice(item: ItemAmount, priceAdjustment: PriceAdjustment?) =
        getMapPrice(mapOf(item.type to item.amount), priceAdjustment)
    override fun getAmountPrice(items: Iterable<ItemAmount>, priceAdjustment: PriceAdjustment?) =
        getMapPrice(items.groupBy { it.type }.mapValues { (_, items) -> items.sumOf { it.amount } }, priceAdjustment)

    override fun getMapPrice(items: Map<Material, Double>, priceAdjustment: PriceAdjustment?) =
        adjustPrice(
            items.entries.sumOfNullable { (type, amount) -> prices[type]?.let { it * amount } },
            priceAdjustment)
}
