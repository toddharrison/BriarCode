package com.briarcraft.econ.price

import com.briarcraft.econ.api.item.ItemAmount
import com.briarcraft.econ.api.price.DerivedPricingManager
import com.briarcraft.econ.api.price.Price
import com.briarcraft.econ.api.price.PriceAdjustment
import com.briarcraft.econ.api.stock.ReducingStockManager
import com.briarcraft.kotlin.util.toEnumSet
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class DerivedDynamicPricingManager(
    override val stock: ReducingStockManager,
    basePrices: Map<Material, Price>,
    override val defaultCurve: Double,
    override val defaultMinPriceMultiplier: Double
): DerivedPricingManager<ReducingStockManager> {
    private val basePrices: Map<Material, Price> = HashMap(basePrices)
    private val baseItems: Set<Material> = stock.getBaseItems().toEnumSet()

    init {
        require(baseItems == basePrices.keys)
        require((-1.0..1.0).contains(defaultCurve))
        basePrices.values.forEach { price ->
            require(price.maxPrice > 0)
            if (price.minPrice != null) require(price.minPrice!! > 0)
            if (price.curve != null) require((-1.0..1.0).contains(price.curve!!))
        }
    }

    override fun getUnitPrice(type: Material, priceAdjustment: PriceAdjustment?) =
        adjustPrice(getMapPrice(mapOf(type to 1.0)), priceAdjustment)
    override fun getMaxPrice(type: Material, priceAdjustment: PriceAdjustment?)=
        adjustPrice(basePrices[type]?.maxPrice, priceAdjustment)
    override fun getStackPrice(item: ItemStack, priceAdjustment: PriceAdjustment?) =
        getMapPrice(mapOf(item.type to item.amount.toDouble()), priceAdjustment)
    override fun getStackPrice(items: Iterable<ItemStack>, priceAdjustment: PriceAdjustment?) =
        getMapPrice(items.groupBy { it.type }.mapValues { (_, items) -> items.sumOf { it.amount }.toDouble() }, priceAdjustment)
    override fun getAmountPrice(item: ItemAmount, priceAdjustment: PriceAdjustment?) =
        getMapPrice(mapOf(item.type to item.amount), priceAdjustment)
    override fun getAmountPrice(items: Iterable<ItemAmount>, priceAdjustment: PriceAdjustment?) =
        getMapPrice(items.groupBy { it.type }.mapValues { (_, items) -> items.sumOf { it.amount } }, priceAdjustment)

    override fun getMapPrice(items: Map<Material, Double>, priceAdjustment: PriceAdjustment?) =
        adjustPrice(getMapPrice(items), priceAdjustment)



    private fun getMapPrice(items: Map<Material, Double>) = stock.reduce(items).let {
        if (baseItems.containsAll(it.keys)) {
            it.map { (type, amount) -> getAdjustedPrice(type, basePrices[type]!!)!! * amount }
                .sum()
        } else null
    }
}
