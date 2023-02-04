package com.briarcraft.econ.price

import com.briarcraft.econ.api.item.ItemAmount
import com.briarcraft.econ.api.price.DerivedPricingManager
import com.briarcraft.econ.api.price.Price
import com.briarcraft.econ.api.price.PriceAdjustment
import com.briarcraft.econ.api.stock.StockManager
import com.briarcraft.kotlin.util.sumOfNullable
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class DynamicPricingManager(
    override val stock: StockManager,
    prices: Map<Material, Price>,
    override val defaultCurve: Double,
    override val defaultMinPriceMultiplier: Double
): DerivedPricingManager<StockManager> {
    private val prices: Map<Material, Price> = HashMap(prices)

    init {
        require(stock.getItems().containsAll(prices.keys))
        require((-1.0..1.0).contains(defaultCurve))
        prices.values.forEach { price ->
            require(price.maxPrice > 0)
            if (price.curve != null) require((-1.0..1.0).contains(price.curve!!))
        }
    }

    override fun getUnitPrice(type: Material, priceAdjustment: PriceAdjustment?) =
        adjustPrice(
            prices[type]?.let { getAdjustedPrice(type, it) },
            priceAdjustment)
    override fun getMaxPrice(type: Material, priceAdjustment: PriceAdjustment?) =
        adjustPrice(
            prices[type]?.maxPrice,
            priceAdjustment)

    override fun getStackPrice(item: ItemStack, priceAdjustment: PriceAdjustment?) =
        getMapPrice(mapOf(item.type to item.amount.toDouble()), priceAdjustment)
    override fun getStackPrice(items: Iterable<ItemStack>, priceAdjustment: PriceAdjustment?) =
        getMapPrice(items.groupBy { it.type }.mapValues { (_, items) -> items.sumOf { it.amount }.toDouble() }, priceAdjustment)
    override fun getAmountPrice(item: ItemAmount, priceAdjustment: PriceAdjustment?) =
        getMapPrice(mapOf(item.type to item.amount), priceAdjustment)
    override fun getAmountPrice(items: Iterable<ItemAmount>, priceAdjustment: PriceAdjustment?) =
        getMapPrice(items.groupBy { it.type }.mapValues { (_, amounts) -> amounts.sumOf { it.amount } }, priceAdjustment)

    override fun getMapPrice(items: Map<Material, Double>, priceAdjustment: PriceAdjustment?) =
        adjustPrice(
            items.entries.sumOfNullable { (type, amount) ->
                prices[type]?.let { getAdjustedPrice(type, it)?.times(amount) } },
            priceAdjustment)
            .also { println(items) }
}
