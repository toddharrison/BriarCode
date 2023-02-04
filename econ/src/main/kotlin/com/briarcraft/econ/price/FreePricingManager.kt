package com.briarcraft.econ.price

import com.briarcraft.econ.api.item.ItemAmount
import com.briarcraft.econ.api.price.PriceAdjustment
import com.briarcraft.econ.api.price.PricingManager
import com.briarcraft.econ.api.stock.StockManager
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class FreePricingManager(override val stock: StockManager): PricingManager {
    override fun getUnitPrice(type: Material, priceAdjustment: PriceAdjustment?) =
        if (stock.getItems().contains(type)) {
            adjustPrice(0.0, priceAdjustment)
        } else null

    override fun getMaxPrice(type: Material, priceAdjustment: PriceAdjustment?) =
        if (stock.getItems().contains(type)) {
            adjustPrice(0.0, priceAdjustment)
        } else null
    override fun getStackPrice(item: ItemStack, priceAdjustment: PriceAdjustment?) =
        if (stock.getItems().contains(item.type)) {
            adjustPrice(0.0, priceAdjustment)
        } else null
    override fun getStackPrice(items: Iterable<ItemStack>, priceAdjustment: PriceAdjustment?) =
        if (stock.getItems().containsAll(items.map { it.type })) {
            adjustPrice(0.0, priceAdjustment)
        } else null
    override fun getAmountPrice(item: ItemAmount, priceAdjustment: PriceAdjustment?) =
        if (stock.getItems().contains(item.type)) {
            adjustPrice(0.0, priceAdjustment)
        } else null
    override fun getAmountPrice(items: Iterable<ItemAmount>, priceAdjustment: PriceAdjustment?) =
        if (stock.getItems().containsAll(items.map { it.type })) {
            adjustPrice(0.0, priceAdjustment)
        } else null

    override fun getMapPrice(items: Map<Material, Double>, priceAdjustment: PriceAdjustment?) =
        if (stock.getItems().containsAll(items.keys)) {
            adjustPrice(0.0, priceAdjustment)
        } else null
}
