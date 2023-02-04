package com.briarcraft.econ.stock

import com.briarcraft.econ.api.item.ItemAmount
import com.briarcraft.econ.api.stock.Stock
import com.briarcraft.econ.api.stock.StockManager
import com.briarcraft.kotlin.util.allNullable
import com.briarcraft.kotlin.util.toEnumSet
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class InfiniteStockManager(items: Set<Material>): StockManager {
    private val items = items.toEnumSet()

    override fun getItems(): Set<Material> = items
    override fun getStock(type: Material) = if (items.contains(type)) {
        Stock()
    } else null
    override fun add(item: ItemStack) = if (items.contains(item.type)) true else null
    // TODO Optimize
    override fun add(items: Iterable<ItemStack>) = if (canAdd(items) == true) {
        items.allNullable { add(it) }
    } else null
    override fun add(item: ItemAmount) = if (items.contains(item.type)) true else null
    override fun canAdd(item: ItemStack) = if (items.contains(item.type)) true else null
    override fun canAdd(items: Iterable<ItemStack>) = items.allNullable { canAdd(it) }
    override fun canAdd(item: ItemAmount) = if (items.contains(item.type)) true else null
    override fun remove(item: ItemStack) = if (items.contains(item.type)) true else null
    // TODO Optimize
    override fun remove(items: Iterable<ItemStack>) = if (canRemove(items) == true) {
        items.allNullable { remove(it) }
    } else null
    override fun remove(item: ItemAmount) = if (items.contains(item.type)) true else null
    override fun canRemove(item: ItemStack) = if (items.contains(item.type)) true else null
    override fun canRemove(items: Iterable<ItemStack>) = items.allNullable { canRemove(it) }
    override fun canRemove(item: ItemAmount) = if (items.contains(item.type)) true else null
}
